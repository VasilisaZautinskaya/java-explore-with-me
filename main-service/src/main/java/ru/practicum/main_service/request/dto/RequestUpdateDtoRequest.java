package ru.practicum.main_service.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main_service.utils.Status;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestUpdateDtoRequest {

    List<Long> requestIds;

    Status status;
}

