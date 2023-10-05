package ru.ptacticum.main_service.user.mapper;

import lombok.experimental.UtilityClass;
import ru.ptacticum.main_service.user.dto.UserDto;
import ru.ptacticum.main_service.user.dto.UserShortDto;
import ru.ptacticum.main_service.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
        return userDto;
    }

    public UserShortDto toUser(User user) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        return userShortDto;
    }
}