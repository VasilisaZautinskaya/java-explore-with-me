package ru.practicum.main_service.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.UnionService;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UnionService unionService;

    public void deleteUser(long userId) {
        unionService.getUserOrNotFound(userId);
        userRepository.deleteById(userId);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsers(List<Long> ids, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        if (ids == null) {
            return userRepository.findAll(pageRequest).toList();
        } else {
            return userRepository.findByIdInOrderByIdAsc(ids, pageRequest);
        }
    }
}
