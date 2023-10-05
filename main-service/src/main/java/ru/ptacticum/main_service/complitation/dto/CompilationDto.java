package ru.ptacticum.main_service.complitation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.ptacticum.main_service.event.dto.EventShortDto;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {

    Long id;

    Boolean pinned;

    String title;

    Set<EventShortDto> events;
}
