package ru.practicum.stats_server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats_dto.HitDto;
import ru.practicum.stats_dto.ViewStatsDto;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.model.ViewStats;

import java.util.ArrayList;
import java.util.List;


@Mapper(componentModel = "spring")
public class StatsMapper {
    public static ViewStatsDto toStatsResponseDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

    public static Stats toStats(HitDto hitDto) {
        return Stats.builder()
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .uri(hitDto.getUri())
                .app(hitDto.getApp())
                .build();
    }

    public static HitDto toHitDto(Stats stats) {
        return HitDto.builder()
                .ip(stats.getIp())
                .timestamp(stats.getTimestamp())
                .uri(stats.getUri())
                .app(stats.getApp())
                .build();
    }

    public static List<ViewStatsDto> toViewStatsDtoList(List<ViewStats> viewStats) {
        List<ViewStatsDto> dtolist = new ArrayList<>();
        for (ViewStats viewStats1 : viewStats) {
            ViewStatsDto viewStatsDto = toStatsResponseDto(viewStats1);
            dtolist.add(viewStatsDto);
        }
        return dtolist;
    }
}
