package ru.practicum.mainservice.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.utils.Status;
import ru.practicum.mainservice.request.dto.RequestDto;
import ru.practicum.mainservice.request.dto.RequestUpdateDtoResult;
import ru.practicum.mainservice.request.model.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {
    public RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
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