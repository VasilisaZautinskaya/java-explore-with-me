package ru.ptacticum.main_service.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ptacticum.main_service.utils.Status;

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

