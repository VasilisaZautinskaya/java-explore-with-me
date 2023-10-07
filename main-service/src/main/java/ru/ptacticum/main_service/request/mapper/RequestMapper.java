package ru.ptacticum.main_service.request.mapper;

import lombok.experimental.UtilityClass;
import ru.ptacticum.main_service.event.model.Event;
import ru.ptacticum.main_service.request.dto.RequestDto;
import ru.ptacticum.main_service.request.dto.RequestUpdateDtoResult;
import ru.ptacticum.main_service.request.model.Request;
import ru.ptacticum.main_service.user.model.User;
import ru.ptacticum.main_service.utils.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public RequestDto toRequestDto(Request request) {
        RequestDto requestDto = RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
        return requestDto;
    }

    public Request toRequest(RequestDto requestDto, Event event, User user) {
        Request request = Request.builder()
                .id(requestDto.getId())
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(Status.PENDING)
                .build();
        return request;
    }

    public List<RequestDto> toRequestDtoList(Iterable<Request> requests) {
        List<RequestDto> result = new ArrayList<>();

        for (Request request : requests) {
            result.add(toRequestDto(request));
        }
        return result;
    }

    public RequestUpdateDtoResult toRequestUpdateDto(List<Request> requestList) {
        RequestUpdateDtoResult result = RequestUpdateDtoResult.builder()
                .confirmedRequests(toRequestDtoList(requestList.stream().filter(request -> request.getStatus().equals(Status.CONFIRMED)).collect(Collectors.toList())))
                .rejectedRequests(toRequestDtoList(requestList.stream().filter(request -> request.getStatus().equals(Status.REJECTED)).collect(Collectors.toList())))
                .build();
        return result;
    }
}