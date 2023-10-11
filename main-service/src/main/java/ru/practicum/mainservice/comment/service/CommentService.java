package ru.practicum.mainservice.comment.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.UnionService;
import ru.practicum.mainservice.comment.dto.CommentNewDto;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.ValidationException;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.utils.State;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CommentService {

    private final UnionService unionService;
    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Transactional
    public Comment addComment(Long userId, Long eventId, CommentNewDto commentNewDto) {

        User user = unionService.getUserOrNotFound(userId);
        Event event = unionService.getEventOrNotFound(eventId);

        Comment comment = commentMapper.toComment(commentNewDto, user, event);


        return commentRepository.save(comment);

    }

    @Transactional
    public Comment updateComment(Long userId, Long commentId, String message) {

        Comment comment = unionService.getCommentOrNotFound(commentId);

        if (!userId.equals(comment.getUser().getId())) {
            throw new ConflictException(String.format("User %s is not the author of the comment %s.", userId, commentId));
        }

        if (message != null && !message.isBlank()) {
            comment.setMessage(message);
        }


        return commentRepository.save(comment);
    }

    @Transactional
    public void deletePrivateComment(Long userId, Long commentId) {

        Comment comment = unionService.getCommentOrNotFound(commentId);
        unionService.getUserOrNotFound(userId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ConflictException(String.format("User %s is not the author of the comment %s.", userId, commentId));
        }

        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByUserId(String rangeStart, String rangeEnd, Long userId, Integer from, Integer size) {

        unionService.getUserOrNotFound(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Start must be after End");
            }
            if (endTime.isAfter(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now())) {
                throw new ValidationException("date must be the past");
            }
        }

        List<Comment> commentList = commentRepository.getCommentsByUserId(userId, startTime, endTime, pageRequest);

        return commentList;
    }

    public List<Comment> getComments(String rangeStart, String rangeEnd, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Start must be after End");
            }
            if (endTime.isAfter(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now())) {
                throw new ValidationException("date must be the past");
            }
        }

        List<Comment> commentList = commentRepository.getComments(startTime, endTime, pageRequest);

        return commentList;
    }

    @Transactional
    public void deleteAdminComment(Long commentId) {

        unionService.getCommentOrNotFound(commentId);
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByEventId(String rangeStart, String rangeEnd, Long eventId, Integer from, Integer size) {

        unionService.getEventOrNotFound(eventId);
        PageRequest pageRequest = PageRequest.of(from / size, size);

        LocalDateTime startTime = unionService.parseDate(rangeStart);
        LocalDateTime endTime = unionService.parseDate(rangeEnd);

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                throw new ValidationException("Start must be after End");
            }
            if (endTime.isAfter(LocalDateTime.now()) || startTime.isAfter(LocalDateTime.now())) {
                throw new ValidationException("date must be the past");
            }
        }

        List<Comment> commentList = commentRepository.getCommentsByEventId(eventId, startTime, endTime, pageRequest);

        return commentList;
    }
}