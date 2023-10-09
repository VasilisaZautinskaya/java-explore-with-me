package ru.practicum.mainservice.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.utils.Status;

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

