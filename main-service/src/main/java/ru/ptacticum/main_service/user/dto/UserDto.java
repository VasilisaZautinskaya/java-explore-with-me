package ru.ptacticum.main_service.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotBlank
    String email;

    @NotNull
    Long id;

    @NotBlank
    String name;
}
