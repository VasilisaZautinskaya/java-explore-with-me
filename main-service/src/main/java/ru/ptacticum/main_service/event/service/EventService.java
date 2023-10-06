package ru.ptacticum.main_service.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_server.model.ViewStats;
import ru.ptacticum.main_service.UnionService;
import ru.ptacticum.main_service.event.model.Event;
import ru.ptacticum.main_service.event.repository.EventRepository;
import ru.ptacticum.main_service.event.repository.LocationRepository;
import ru.ptacticum.main_service.exception.ConflictException;
import ru.ptacticum.main_service.exception.NotFoundException;
import ru.ptacticum.main_service.exception.ValidationException;
import ru.ptacticum.main_service.request.dto.RequestUpdateDtoRequest;
import ru.ptacticum.main_service.request.dto.RequestUpdateDtoResult;
import ru.ptacticum.main_service.request.mapper.RequestMapper;
import ru.ptacticum.main_service.request.model.Request;
import ru.ptacticum.main_service.request.repository.RequestRepository;
import ru.ptacticum.main_service.user.model.User;
import ru.ptacticum.main_service.utils.State;
import ru.ptacticum.main_service.utils.StateAction;
import ru.ptacticum.main_service.utils.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.ptacticum.main_service.utils.State.PUBLISHED;

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
        if (oldEvent.getState().equals(PUBLISHED)) {
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

        return requestRepository.findByEventId(eventId);
    }

    public RequestUpdateDtoResult updateStatusRequestsForEventIdByUserId(RequestUpdateDtoRequest requestDto, Long userId, Long eventId) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        RequestUpdateDtoResult result = RequestUpdateDtoResult.builder()
                .confirmedRequests(Collections.emptyList())
                .rejectedRequests(Collections.emptyList())
                .build();

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("User %s is not the initiator of the event %s.", userId, eventId));
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return result;
        }

        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Exceeded the limit of participants");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        long vacantPlace = event.getParticipantLimit() - event.getConfirmedRequests();

        List<Request> requestsList = requestRepository.findAllById(requestDto.getRequestIds());

        for (Request request : requestsList) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Request must have status PENDING");
            }   -

            if (requestDto.getStatus().equals(Status.CONFIRMED) && vacantPlace > 0) {
                request.setStatus(Status.CONFIRMED);
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
                confirmedRequests.add(request);
                vacantPlace--;
            } else {
                request.setStatus(Status.REJECTED);
                rejectedRequests.add(request);
            }
        }
        result.setConfirmedRequests(RequestMapper.toRequestDtoList(confirmedRequests));
        result.setRejectedRequests(RequestMapper.toRequestDtoList(rejectedRequests));

        eventRepository.save(event);
        requestRepository.saveAll(requestsList);

        return result;
    }


    public Event updateEventByAdmin(Event event, Long eventId) {

        Event newEvent = unionService.getEventOrNotFound(eventId);

        if (event.getState() != null) {
            if (event.getState().equals(StateAction.PUBLISH_EVENT)) {

                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Event - %s, has already been published, cannot be published again ", event.getTitle()));
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(State.PUBLISHED);

            } else {

                if (!event.getState().equals(State.PENDING)) {
                    throw new ConflictException(String.format("Event - %s, cannot be canceled because its statute is not \"PENDING\"", event.getTitle()));
                }
                event.setState(State.CANCELED);
            }
        }

        return baseUpdateEvent(event, event);
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
        if (!event.getState().equals(PUBLISHED)) {
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
            if (newEvent.getState() == PUBLISHED) {
                oldEvent.setState(PUBLISHED);
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
        client.addHit(hitDto);
    }

    private Long getViewsEventById(Long eventId) {

        String uri = "/events/" + eventId;
        ResponseEntity<Object> response = statsClient.getStats(START_HISTORY, LocalDateTime.now(), uri, true);
        List<ViewStats> result = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });

        if (result.isEmpty()) {
            return 0L;
        } else {
            return result.get(0).getHits();
        }
    }
}
