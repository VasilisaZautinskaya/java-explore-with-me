package ru.practicum.main_service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.complitation.model.Compilation;
import ru.practicum.main_service.complitation.repository.CompilationRepository;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.request.model.Request;
import ru.practicum.main_service.request.repository.RequestRepository;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UnionService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public User getUserOrNotFound(Long userId) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new NotFoundException(User.class, "Пользователь с айди " + userId + " не найден.");
        } else {
            return user.get();
        }
    }

    public Category getCategoryOrNotFound(Long categoryId) {

        Optional<Category> category = categoryRepository.findById(categoryId);

        if (category.isEmpty()) {
            throw new NotFoundException(Category.class, "Категория с айди " + categoryId + " не найдена.");
        } else {
            return category.get();
        }
    }

    public Event getEventOrNotFound(Long eventId) {

        Optional<Event> event = eventRepository.findById(eventId);

        if (event.isEmpty()) {
            throw new NotFoundException(Event.class, "Событие с айди " + eventId + " не найдено.");
        } else {
            return event.get();
        }
    }

    public Request getRequestOrNotFound(Long requestId) {

        Optional<Request> request = requestRepository.findById(requestId);

        if (request.isEmpty()) {
            throw new NotFoundException(Request.class, "Запрос с айди " + requestId + " не найден.");
        } else {
            return request.get();
        }
    }

    public Compilation getCompilationOrNotFound(Long compId) {

        Optional<Compilation> compilation = compilationRepository.findById(compId);

        if (compilation.isEmpty()) {
            throw new NotFoundException(Compilation.class, "Compilation id " + compId + " not found.");
        } else {
            return compilation.get();
        }
    }

    public LocalDateTime parseDate(String date) {

        if (date != null) {
            return LocalDateTime.parse(date, FORMATTER);
        } else {
            return null;
        }
    }
}
