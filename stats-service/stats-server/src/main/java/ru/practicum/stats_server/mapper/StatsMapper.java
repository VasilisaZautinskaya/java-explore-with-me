package ru.practicum.stats_server.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.ViewStatsDto;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.model.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class StatsMapper {

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(1L)
                .build();
    }

    public static Stats toStats(HitDto hitDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return Stats.builder()
                .ip(hitDto.getIp())
                .timestamp(LocalDateTime.parse(hitDto.getTimestamp(), formatter))
                .uri(hitDto.getUri())
                .app(hitDto.getApp())
                .build();
    }


    public static List<ViewStatsDto> toViewStatsDtoList(List<ViewStats> viewStats) {
        List<ViewStatsDto> dtolist = new ArrayList<>();
        for (ViewStats viewStats1 : viewStats) {
            ViewStatsDto viewStatsDto = toViewStatsDto(viewStats1);
            dtolist.add(viewStatsDto);
        }
        return dtolist;
    }
}
