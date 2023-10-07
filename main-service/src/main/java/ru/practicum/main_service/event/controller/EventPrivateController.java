package ru.practicum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.EventNewDto;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.request.mapper.RequestMapper;
import ru.practicum.main_service.utils.Status;
import ru.practicum.main_service.event.mapper.EventMapper;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.request.dto.RequestDto;
import ru.practicum.main_service.request.dto.RequestUpdateDtoRequest;
import ru.practicum.main_service.request.dto.RequestUpdateDtoResult;
import ru.practicum.main_service.request.model.Request;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventService eventService;
    private final EventMapper eventMapper;



    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto addEvent(@Valid @RequestBody EventNewDto eventNewDto,
                                 @PathVariable Long userId) {

        log.info("User id {}, add Event {} ", userId, eventNewDto.getAnnotation());
        Event event = eventMapper.fromNewDto(eventNewDto, userId);
        return eventMapper.toEventFullDto(eventService.addEvent(event));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable Long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("List events for User Id {}. Where from = {}, size = {}", userId, from, size);
        return eventMapper.toEventShortDtoList(eventService.getAllEventsByUserId(userId, from, size));
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {

        log.info("Get Event id {}, for User id {} ", eventId, userId);
        return eventMapper.toEventFullDto(eventService.getUserEventById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEventByUserId(@RequestBody @Valid EventNewDto eventNewDto,
                                            @PathVariable Long userId,
                                            @PathVariable Long eventId) {

        log.info("User id {}, update Event {} ", eventId, eventNewDto.getAnnotation());
        Event newEvent = eventMapper.fromNewDto(eventNewDto, userId);
        return eventMapper.toEventFullDto(eventService.updateEventByUserId(newEvent, userId, eventId));
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    private List<RequestDto> getRequestsForEventIdByUserId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {

        log.info("Get all requests for event id{} by user Id{}.", eventId, userId);
        return RequestMapper.toRequestDtoList(eventService.getRequestsForEventIdByUserId(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    private RequestUpdateDtoResult updateStatusRequestsForEventIdByUserId(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @RequestBody RequestUpdateDtoRequest requestDto) {
        log.info("Update status request for event id{}, by user id{}.", eventId, userId);
        List<Request> requests = eventService.getRequestsById(requestDto.getRequestIds());
        Status status = requestDto.getStatus();
        return RequestMapper.toRequestUpdateDto(eventService.updateStatusRequestsForEventIdByUserId(requests, userId, eventId, status));
    }
}