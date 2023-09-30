package ru.practicum.stats_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.ViewStatsDto;
import ru.practicum.stats_server.mapper.StatsMapper;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public HitDto addHit(@Valid @RequestBody Stats stats) {
        return StatsMapper.toHitDto(statsService.addHit(stats));
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam("start")
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam("end")
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(name = "uri", required = false, defaultValue = "")
                                       List<String> uri,
                                       @RequestParam(name = "unique", required = false, defaultValue = "false")
                                       Boolean unique) {

        return StatsMapper.toViewStatsDtoList(statsService.getStats(start, end, uri, unique));
    }
}
