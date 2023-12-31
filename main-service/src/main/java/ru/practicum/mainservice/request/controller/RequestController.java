package ru.practicum.mainservice.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.request.dto.RequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId,
                                 @RequestParam Long eventId) {

        log.info("Пользователь с id {} добавил запрос в событие {}.", userId, eventId);
        return RequestMapper.toRequestDto(requestService.addRequest(userId, eventId));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestDto> getRequestsByUserId(@PathVariable Long userId) {

        log.info("Поулчение списка всех запросов по id пользователя{}.", userId);
        return RequestMapper.toRequestDtoList(requestService.getRequestsByUserId(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {

        log.info("Пользователь с id{} отменил запрос c id{}.", userId, requestId);
        return RequestMapper.toRequestDto(requestService.cancelRequest(userId, requestId));
    }
}
