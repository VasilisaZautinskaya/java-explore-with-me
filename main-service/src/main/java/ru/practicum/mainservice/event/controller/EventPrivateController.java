package ru.practicum.mainservice.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.EventNewDto;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.EventUpdateDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.utils.Status;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.service.EventService;
import ru.practicum.mainservice.request.dto.RequestDto;
import ru.practicum.mainservice.request.dto.RequestUpdateDtoRequest;
import ru.practicum.mainservice.request.dto.RequestUpdateDtoResult;
import ru.practicum.mainservice.request.model.Request;

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

        log.info("Пользователь с id {}, добавляет событие {} ", userId, eventNewDto.getAnnotation());
        Event event = eventMapper.fromNewDto(eventNewDto, userId);
        return eventMapper.toEventFullDto(eventService.addEvent(event));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUserId(@PathVariable Long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Получение списка событий для пользователя с id {}. from = {}, size = {}", userId, from, size);
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
    public EventFullDto updateEventByUserId(@RequestBody @Valid EventUpdateDto eventUpdateDto,
                                            @PathVariable Long userId,
                                            @PathVariable Long eventId) {

        log.info("Пользователь с id{}, обновил событие {} ", eventId, eventUpdateDto.getAnnotation());
        Event newEvent = eventMapper.toEventUpdate(eventUpdateDto, userId);
        return eventMapper.toEventFullDto(eventService.updateEventByUserId(
                newEvent,
                userId,
                eventId,
                eventUpdateDto.getStateAction()
        ));
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    private List<RequestDto> getRequestsForEventIdByUserId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {

        log.info("Получение всех запросов события с id{} для пользователя с id{}.", eventId, userId);
        return RequestMapper.toRequestDtoList(eventService.getRequestsForEventIdByUserId(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(value = HttpStatus.OK)
    private RequestUpdateDtoResult updateStatusRequestsForEventIdByUserId(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @RequestBody RequestUpdateDtoRequest requestDto) {
        log.info("UОбновление статуса события с id{}, пользователем с id{}.", eventId, userId);
        List<Request> requests = eventService.getRequestsById(requestDto.getRequestIds());
        Status status = requestDto.getStatus();
        return RequestMapper.toRequestUpdateDto(eventService.updateStatusRequestsForEventIdByUserId(requests, userId, eventId, status));
    }
}