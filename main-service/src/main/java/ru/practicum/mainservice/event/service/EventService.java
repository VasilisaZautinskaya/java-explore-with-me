package ru.practicum.mainservice.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.UnionService;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.exception.ValidationException;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.utils.State;
import ru.practicum.mainservice.utils.StateAction;
import ru.practicum.mainservice.utils.Status;
import ru.practicum.statsdto.HitDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.event.repository.LocationRepository;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.statsdto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.practicum.statsclient.StatsClient.DT_FORMATTER;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EventService {
    private final UnionService unionService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final LocalDateTime START_HISTORY = LocalDateTime.of(1970, 1, 1, 0, 0);

    @Transactional
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

    @Transactional
    public Event updateEventByUserId(Event newEvent, Long userId, Long eventId, StateAction stateAction) {

        User updater = unionService.getUserOrNotFound(userId);
        Event oldEvent = unionService.getEventOrNotFound(eventId);

        if (!updater.getId().equals(oldEvent.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %s не является инициатором события %s.", userId, eventId));
        }
        if (oldEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format("Пользователь %s не может обновить данное событие %s", userId, eventId));
        }

        return baseUpdateEvent(oldEvent, newEvent, stateAction);
    }

    public List<Request> getRequestsForEventIdByUserId(Long userId, Long eventId) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        if (!user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь %s не является инициатором события %s.", userId, eventId));
        }

        return requestRepository.getByEventId(eventId);
    }

    @Transactional
    public List<Request> getRequestsById(List<Long> requestIds) {
        return requestRepository.findAllById(requestIds);
    }

    @Transactional
    public List<Request> updateStatusRequestsForEventIdByUserId(
            List<Request> requests,
            Long userId,
            Long eventId,
            Status status
    ) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

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

        for (Request request : requests) {
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
        requestRepository.saveAll(requests);

        return requests;
    }

    @Transactional
    public Event updateEventByAdmin(StateAction stateAction, Long eventId, Event event) {

        Event oldEvent = unionService.getEventOrNotFound(eventId);

        if (stateAction != null) {
            if (stateAction.equals(StateAction.PUBLISH_EVENT)) {

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

        return baseUpdateEvent(oldEvent, event, stateAction);
    }

    public List<Event> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String
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
            throw new NotFoundException(Event.class, String.format("Событие %s не опубликовано", eventId));
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
                throw new ValidationException("Время окончания не может быть раньше времени начала");
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

    private Event baseUpdateEvent(Event oldEvent, Event newEvent, StateAction stateAction) {

        if (newEvent.getAnnotation() != null && !newEvent.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getCategory() != null) {
            oldEvent.setCategory(unionService.getCategoryOrNotFound(newEvent.getCategory().getId()));
        }
        if (newEvent.getDescription() != null && !oldEvent.getDescription().isBlank()) {
            oldEvent.setDescription(newEvent.getDescription());
        }
        if (newEvent.getEventDate() != null) {
            oldEvent.setEventDate(newEvent.getEventDate());
        }
        if (newEvent.getLocation() != null) {
            oldEvent.setLocation(newEvent.getLocation());
        }
        if (newEvent.getPaid() != null) {
            oldEvent.setPaid(newEvent.getPaid());
        }
        if (newEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(newEvent.getRequestModeration());
        }
        if (stateAction != null) {
            if (stateAction == StateAction.PUBLISH_EVENT) {
                oldEvent.setState(State.PUBLISHED);
                oldEvent.setPublishedOn(LocalDateTime.now());
            } else if (stateAction == StateAction.REJECT_EVENT || stateAction == StateAction.CANCEL_REVIEW) {
                oldEvent.setState(State.CANCELED);
            } else if (stateAction == StateAction.SEND_TO_REVIEW) {
                oldEvent.setState(State.PENDING);
            }
        }
        if (newEvent.getTitle() != null && !newEvent.getTitle().isBlank()) {
            oldEvent.setTitle(newEvent.getTitle());
        }
        if (newEvent.getDescription() != null && !newEvent.getDescription().isBlank()) {
            oldEvent.setDescription(newEvent.getDescription());
        }

        locationRepository.save(oldEvent.getLocation());
        return eventRepository.save(oldEvent);
    }

    private void sendInfo(String uri, String ip) {
        HitDto hitDto = HitDto.builder()
                .app("main-service")
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().format(DT_FORMATTER))
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
