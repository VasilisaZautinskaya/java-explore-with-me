package ru.ptacticum.main_service.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ptacticum.main_service.request.dto.RequestDto;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestUpdateDtoResult {

    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}