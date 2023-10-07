package ru.practicum.main_service.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.UnionService;
import ru.practicum.main_service.exception.ConflictException;
import ru.practicum.main_service.request.repository.RequestRepository;
import ru.practicum.main_service.utils.State;
import ru.practicum.main_service.utils.Status;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.request.model.Request;
import ru.practicum.main_service.user.model.User;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UnionService unionService;

    @Transactional
    public Request addRequest(Long userId, Long eventId) throws ConflictException {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        if (event.getParticipantLimit() <= event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("Event %s requests exceed the limit", event));
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(String.format("Initiator, user id %s cannot give a request to participate in his event", user.getId()));
        }

        if (requestRepository.getByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException(String.format("You have already applied to participate in Event %s", event.getTitle()));
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException(String.format("Event %s has not been published, you cannot request participation", eventId));
        } else {

            Request request = Request.builder()
                    .requester(user)
                    .event(event)
                    .created(LocalDateTime.now())
                    .status(Status.PENDING)
                    .build();

            if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
                request.setStatus(Status.CONFIRMED);
                request = requestRepository.save(request);
                event.setConfirmedRequests(requestRepository.countAllByEventIdAndStatus(eventId, Status.CONFIRMED));
                eventRepository.save(event);

                return request;
            }

            request = requestRepository.save(request);

            return request;
        }
    }

    public List<Request> getRequestsByUserId(Long userId) {

        unionService.getUserOrNotFound(userId);
        List<Request> requestList = requestRepository.getByRequesterId(userId);

        return requestList;
    }

    @Transactional
    public Request cancelRequest(Long userId, Long requestId) {

        unionService.getUserOrNotFound(userId);
        Request request = unionService.getRequestOrNotFound(requestId);
        request.setStatus(Status.CANCELED);

        return requestRepository.save(request);
    }
}
