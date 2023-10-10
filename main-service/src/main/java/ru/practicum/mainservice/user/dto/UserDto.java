package ru.practicum.mainservice.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    Long id;

    @NotBlank
    @Email
    @Size(min = 6, max = 254)
    String email;


    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}
