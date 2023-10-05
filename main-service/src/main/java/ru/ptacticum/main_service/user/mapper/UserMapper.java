package ru.ptacticum.main_service.user.mapper;

import lombok.experimental.UtilityClass;
import ru.ptacticum.main_service.request.dto.RequestDto;
import ru.ptacticum.main_service.request.model.Request;
import ru.ptacticum.main_service.user.dto.UserDto;
import ru.ptacticum.main_service.user.dto.UserShortDto;
import ru.ptacticum.main_service.user.model.User;

import java.util.ArrayList;
import java.util.List;

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

    public UserShortDto toUserShortDto(User user) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        return userShortDto;
    }

    public List<UserDto> toUserDtoList(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toUserDto(user));
        }
        return result;
    }
}