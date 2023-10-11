package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.CommentFullDto;
import ru.practicum.mainservice.comment.dto.CommentNewDto;
import ru.practicum.mainservice.comment.dto.CommentShortDto;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/{eventId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentFullDto addComment(@Valid @RequestBody CommentNewDto commentNewDto,
                                     @PathVariable Long userId,
                                     @PathVariable Long eventId) {

        log.info("User id {} add new Comment for Event {} ", userId, eventId);
        return commentMapper.toCommentFullDto(commentService.addComment(userId, eventId, commentNewDto));
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CommentFullDto updateComment(@Valid @RequestBody CommentNewDto commentNewDto,
                                        @PathVariable Long userId,
                                        @PathVariable Long commentId) {

        log.info("User id {} update Comment {} ", userId, commentId);
        return commentMapper.toCommentFullDto(commentService.updateComment(userId, commentId, commentNewDto.getMessage()));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {

        log.info("User id {} delete Comment {} ", userId, commentId);
        commentService.deletePrivateComment(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentShortDto> getCommentsByUserId(@PathVariable Long userId,
                                                     @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                                     @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get all comment by User  id{}, from {} to end {}.", userId, rangeStart, rangeEnd);
        return commentMapper.toCommentShortDtoList(commentService.getCommentsByUserId(rangeStart, rangeEnd, userId, from, size));
    }
}