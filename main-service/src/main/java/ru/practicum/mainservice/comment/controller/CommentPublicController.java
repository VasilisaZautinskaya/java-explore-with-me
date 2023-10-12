package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.CommentShortDto;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentPublicController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping("{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentShortDto> getCommentsByEventId(@PathVariable Long eventId,
                                                      @RequestParam(required = false, name = "rangeStart") String rangeStart,
                                                      @RequestParam(required = false, name = "rangeEnd") String rangeEnd,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get all comment by Event  id{}, from {} to end {}.", eventId, rangeStart, rangeEnd);
        return commentMapper.toCommentShortDtoList(commentService.getCommentsByEventId(rangeStart, rangeEnd, eventId, from, size));
    }
}
