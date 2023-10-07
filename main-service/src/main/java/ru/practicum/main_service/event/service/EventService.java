package ru.practicum.main_service.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.UnionService;
import ru.practicum.stats_client.StatsClient;
import ru.practicum.main_service.exception.ConflictException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.exception.ValidationException;
import ru.practicum.main_service.request.repository.RequestRepository;
import ru.practicum.main_service.utils.State;
import ru.practicum.main_service.utils.StateAction;
import ru.practicum.main_service.utils.Status;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.event.repository.LocationRepository;
import ru.practicum.main_service.request.model.Request;
import ru.practicum.main_service.user.model.User;
import ru.practicum.stats_dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {
    private final UnionService unionService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final LocalDateTime START_HISTORY = LocalDateTime.of(1970, 1, 1, 0, 0);


    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEventsByUserId(Long userId, Integer from, Integer size) {

        unionService.getUserOrNotFound(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return eventRepository.findByInitiatorId(userId, pageRequest);
    }

    public Event getUserEventById(Long userId, Long eventId) {

        unionService.getUserOrNotFound(userId);
        unionService.getEventOrNotFound(eventId);

        return eventRepository.findByInitiatorIdAndId(userId, eventId);
    }

    public Event updateEventByUserId(Event newEvent, Long userId, Long eventId) {

        User updater = unionService.getUserOrNotFound(userId);
        Event oldEvent = unionService.getEventOrNotFound(eventId);

        if (!updater.getId().equals(oldEvent.getInitiator().getId())) {
            throw new ConflictException(String.format("User %s is not the initiator of the event %s.", userId, eventId));
        }
        if (oldEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format("User %s cannot update event %s that has already been published.", userId, eventId));
        }

        return baseUpdateEvent(oldEvent, newEvent);
    }

    public List<Request> getRequestsForEventIdByUserId(Long userId, Long eventId) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("User %s is not the initiator of the event %s.", userId, eventId));
        }

        return requestRepository.getByEventId(eventId);
    }

    public List<Request> getRequestsById(List<Long> requestIds) {
        List<Request> requests = requestRepository.findAllById(requestIds);
        return requests;
    }

    public List<Request> updateStatusRequestsForEventIdByUserId(
            List<Request> requests,
            Long userId,
            Long eventId,
            Status status
    ) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);
        List<Request> requestsList = Collections.emptyList();

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("User %s is not the initiator of the event %s.", userId, eventId));
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return Collections.emptyList();
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Exceeded the limit of participants");
        }

        long vacantPlace = event.getParticipantLimit() - event.getConfirmedRequests();

        for (Request request : requestsList) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Request must have status PENDING");
            }

            if (status.equals(Status.CONFIRMED) && vacantPlace > 0) {
                request.setStatus(Status.CONFIRMED);
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
                vacantPlace--;
            } else {
                request.setStatus(Status.REJECTED);
            }
        }

        eventRepository.save(event);
        requestRepository.saveAll(requestsList);

        return requestsList;
    }


    public Event updateEventByAdmin(Event event, Long eventId) {

        Event oldEvent = unionService.getEventOrNotFound(eventId);

        if (oldEvent.getState() != null) {
            if (oldEvent.getState().equals(StateAction.PUBLISH_EVENT)) {

                if (!oldEvent.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Event - %s, has already been published, cannot be published again ", event.getTitle()));
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(State.PUBLISHED);

            } else {

                if (!oldEvent.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Event - %s, cannot be canceled because its statute is not \"PENDING\"", event.getTitle()));
                }
                event.setState(State.CANCELED);
            }
        }

        return baseUpdateEvent(oldEvent, event);
    }

    public List<Event> getEventsByAdmin
            (List<Long> users, List<String> states, List<Long> categories, String rangeStart, String
                    rangeEnd, Integer from, Integer size) {

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        List<State> statesValue = new ArrayList<>();

        if (states != null) {
            for (String state : states) {
                statesValue.add(State.getStateValue(state));
            }
        }

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Start must be after End");
            }
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return eventRepository.findEventsByAdminFromParam(users, statesValue, categories, startTime, endTime, pageRequest);
    }


    public Event getEventById(Long eventId, String uri, String ip) {

        Event event = unionService.getEventOrNotFound(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(Event.class, String.format("Event %s not published", eventId));
        }

        sendInfo(uri, ip);
        event.setViews(getViewsEventById(event.getId()));
        eventRepository.save(event);

        return event;
    }

    public List<Event> getEventsByPublic(String text, List<Long> categories, Boolean paid, String
            rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, String
                                                 uri, String
                                                 ip) {

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Start must be after End");
            }
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findEventsByPublicFromParam(text, categories, paid, startTime, endTime, onlyAvailable, sort, pageRequest);

        sendInfo(uri, ip);
        for (Event event : events) {
            event.setViews(getViewsEventById(event.getId()));
            eventRepository.save(event);
        }

        return events;
    }

    private Event baseUpdateEvent(Event oldEvent, Event newEvent) {

        if (newEvent.getAnnotation() != null && !newEvent.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null) {
            oldEvent.setCategory(unionService.getCategoryOrNotFound(newEvent.getCategory().getId()));
        }
        if (newEvent.getDescription() != null && !oldEvent.getDescription().isBlank()) {
            oldEvent.setDescription(oldEvent.getDescription());
        }
        if (newEvent.getEventDate() != null) {
            oldEvent.setEventDate(oldEvent.getEventDate());
        }
        if (newEvent.getLocation() != null) {
            oldEvent.setLocation(newEvent.getLocation());
        }
        if (newEvent.getPaid() != null) {
            oldEvent.setPaid(oldEvent.getPaid());
        }
        if (newEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(oldEvent.getParticipantLimit());
        }
        if (newEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(oldEvent.getRequestModeration());
        }
        if (newEvent.getState() != null) {
            if (newEvent.getState() == State.PUBLISHED) {
                oldEvent.setState(State.PUBLISHED);
                oldEvent.setPublishedOn(LocalDateTime.now());
            } else if (newEvent.getState() == State.CANCELED) {
                oldEvent.setState(State.CANCELED);
            } else if (newEvent.getState() == State.PENDING) {
                oldEvent.setState(State.PENDING);
            }
        }
        if (newEvent.getTitle() != null && !newEvent.getTitle().isBlank()) {
            oldEvent.setTitle(oldEvent.getTitle());
        }

        locationRepository.save(oldEvent.getLocation());
        return eventRepository.save(oldEvent);
    }

    private void sendInfo(String uri, String ip) {
        HitDto hitDto = HitDto.builder()
                .app("ewm-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().toString())
                .build();
        statsClient.addHit(hitDto);
    }

    private Long getViewsEventById(Long eventId) {

        String uri = "/events/" + eventId;
        ResponseEntity<Object> response = statsClient.getStats(START_HISTORY, LocalDateTime.now(), uri, true);
        List<ViewStatsDto> result = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });

        if (result.isEmpty()) {
            return 0L;
        } else {
            return result.get(0).getHits();
        }
    }
}
