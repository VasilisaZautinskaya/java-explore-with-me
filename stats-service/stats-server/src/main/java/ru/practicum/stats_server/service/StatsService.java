package ru.practicum.stats_server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.model.ViewStats;
import ru.practicum.stats_server.repository.StatsRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepositoryImpl statsRepository;


    public Stats addHit(Stats stats) {
        log.info("Регистрация обращения к {}", stats);
        return statsRepository.save(stats);
    }

    public List<ViewStats> getAllStats() {
        return statsRepository.getAllStats();
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Вывод списка обращений по параметрам start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);

        if (start.isAfter(end)) {
            log.error("Недопустимый временной промежуток.");
            throw new IllegalArgumentException("Недопустимый временной промежуток.");
        }
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statsRepository.getAllStatsDistinctIp(start, end);
            } else {
                return statsRepository.getAllStats();
            }
        } else {
            if (unique) {
                return statsRepository.getStatsByUrisDistinctIp(start, end, uris);
            } else {
                return statsRepository.getStatsByUris(start, end, uris);
            }
        }
    }
}
