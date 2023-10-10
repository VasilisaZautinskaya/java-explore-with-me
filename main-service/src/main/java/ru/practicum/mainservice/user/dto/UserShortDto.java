package ru.practicum.mainservice.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShortDto {

    Long id;

    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}