package ru.practicum.mainservice;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.complitation.model.Compilation;
import ru.practicum.mainservice.complitation.repository.CompilationRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@AllArgsConstructor
public class UnionService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;

    private final CommentRepository commentRepository;

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public User getUserOrNotFound(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(User.class, "Пользователь с айди " + userId + " не найден."));

    }

    public Category getCategoryOrNotFound(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException(Category.class, "Категория с айди " + categoryId + " не найдена."));

    }

    public Event getEventOrNotFound(Long eventId) {

        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(Event.class, "Событие с айди " + eventId + " не найдено."));

    }

    public Request getRequestOrNotFound(Long requestId) {

        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(Request.class, "Запрос с айди " + requestId + " не найден."));

    }

    public Compilation getCompilationOrNotFound(Long compId) {

        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(Compilation.class, "Compilation id " + compId + " not found."));
    }

    public Comment getCommentOrNotFound(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(Comment.class, "Comment id" + commentId + "not found"));

    }

    public LocalDateTime parseDate(String date) {

        if (date != null) {
            return LocalDateTime.parse(date, FORMATTER);
        } else {
            return null;
        }
    }
}
