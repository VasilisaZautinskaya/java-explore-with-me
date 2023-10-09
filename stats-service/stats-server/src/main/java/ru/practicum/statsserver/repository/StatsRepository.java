package ru.practicum.statsserver.repository;

import ru.practicum.statsserver.model.Stats;
import ru.practicum.statsserver.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {
    void save(Stats stats);

    List<ViewStats> getAllStats();

    List<ViewStats> getAllStatsDistinctIp(LocalDateTime start, LocalDateTime end);

    List<ViewStats> getStatsByUrisDistinctIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
