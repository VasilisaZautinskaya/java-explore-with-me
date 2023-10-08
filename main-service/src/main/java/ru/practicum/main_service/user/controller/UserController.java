package ru.practicum.main_service.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.mapper.UserMapper;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody User user) {

        log.info("Добавление пользователя {} ", user.getName());
        return UserMapper.toUserDto(userService.addUser(user));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("List Users, where ids: {}, from = {}, size = {}", ids, from, size);
        return UserMapper.toUserDtoList(userService.getUsers(ids, from, size));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@Positive @PathVariable("userId") Long userId) {

        log.info("User {} deleted ", userId);
        userService.deleteUser(userId);
    }
}
