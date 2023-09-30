package ru.practicum.stats_server.repository;


import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    Stats save(Stats stats);

    List<ViewStats> getAllStats();

    List<ViewStats> getAllStatsIp(LocalDateTime start, LocalDateTime end);

    List<ViewStats> getStatsByUrisIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
