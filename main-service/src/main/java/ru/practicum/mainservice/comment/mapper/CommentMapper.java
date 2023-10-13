package ru.practicum.mainservice.comment.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.comment.dto.CommentFullDto;
import ru.practicum.mainservice.comment.dto.CommentNewDto;
import ru.practicum.mainservice.comment.dto.CommentShortDto;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CommentMapper {
    private final EventMapper eventMapper;

    public Comment toComment(CommentNewDto commentNewDto, User user, Event event) {
        Comment comment = Comment.builder()
                .user(user)
                .event(event)
                .message(commentNewDto.getMessage())
                .created(LocalDateTime.now())
                .build();
        return comment;
    }

    public CommentFullDto toCommentFullDto(Comment comment) {
        CommentFullDto commentFullDto = CommentFullDto.builder()
                .id(comment.getId())
                .user(UserMapper.toUserDto(comment.getUser()))
                .event(eventMapper.toEventFullDto(comment.getEvent()))
                .message(comment.getMessage())
                .created(comment.getCreated())
                .build();
        return commentFullDto;
    }

    public CommentShortDto toCommentShortDto(Comment comment) {
        CommentShortDto commentShortDto = CommentShortDto.builder()
                .userName(comment.getUser().getName())
                .eventTitle(comment.getEvent().getTitle())
                .message(comment.getMessage())
                .created(comment.getCreated())
                .build();
        return commentShortDto;
    }

    public List<CommentFullDto> toCommentFullDtoList(Iterable<Comment> comments) {
        List<CommentFullDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(toCommentFullDto(comment));
        }
        return result;
    }

    public List<CommentShortDto> toCommentShortDtoList(Iterable<Comment> comments) {
        List<CommentShortDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            result.add(toCommentShortDto(comment));
        }
        return result;
    }
}
