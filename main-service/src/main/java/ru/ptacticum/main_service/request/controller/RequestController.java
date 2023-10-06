package ru.ptacticum.main_service.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ptacticum.main_service.request.dto.RequestDto;
import ru.ptacticum.main_service.request.mapper.RequestMapper;
import ru.ptacticum.main_service.request.service.RequestService;

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

        log.info("User id {} added request for Event id {}.", userId, eventId);
        return RequestMapper.toRequestDto(requestService.addRequest(userId, eventId));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<RequestDto> getRequestsByUserId(@PathVariable Long userId) {

        log.info("Get all requests by user id{}.", userId);
        return RequestMapper.toRequestDtoList(requestService.getRequestsByUserId(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(value = HttpStatus.OK)
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {

        log.info("User id{} canceled request id{}.", userId, requestId);
        return RequestMapper.toRequestDto(requestService.cancelRequest(userId, requestId));
    }
}
