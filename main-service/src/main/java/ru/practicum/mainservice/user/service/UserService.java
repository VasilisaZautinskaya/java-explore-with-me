package ru.practicum.mainservice.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.UnionService;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UnionService unionService;

    @Transactional
    public void deleteUser(long userId) {
        unionService.getUserOrNotFound(userId);
        userRepository.deleteById(userId);
    }

    @Transactional
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
