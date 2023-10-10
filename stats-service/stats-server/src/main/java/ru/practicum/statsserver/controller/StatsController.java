package ru.practicum.statsserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.HitDto;
import ru.practicum.statsdto.ViewStatsDto;
import ru.practicum.statsserver.mapper.StatsMapper;
import ru.practicum.statsserver.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody HitDto hitDto) {
         statsService.addHit(StatsMapper.toStats(hitDto));
    }

    @GetMapping("/stats")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {

        return StatsMapper.toViewStatsDtoList(statsService.getStats(start, end, uris, unique));
    }
}
