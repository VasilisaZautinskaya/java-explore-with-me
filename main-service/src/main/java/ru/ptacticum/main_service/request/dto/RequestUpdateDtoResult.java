package ru.ptacticum.main_service.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

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