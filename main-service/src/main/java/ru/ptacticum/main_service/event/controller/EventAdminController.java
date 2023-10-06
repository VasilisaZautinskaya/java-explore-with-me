package ru.ptacticum.main_service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ptacticum.main_service.event.dto.EventFullDto;
import ru.ptacticum.main_service.event.mapper.EventMapper;
import ru.ptacticum.main_service.event.model.Event;
import ru.ptacticum.main_service.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false, name = "users") List<Long> users,
                                               @RequestParam(required = false, name = "states") List<String> states,
                                               @RequestParam(required = false, name = "categories") List<Long> categories,
                                               @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                               @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get all events with parameters: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}", users, states, categories, rangeStart, rangeEnd, from, size);
        return eventMapper.toEventFullDtoList(eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@Valid @RequestBody Event event,
                                           @PathVariable Long eventId) {

        log.info("Admin update Event {} ", eventId);
        return eventMapper.toEventFullDto(eventService.updateEventByAdmin(event, eventId));
    }
}