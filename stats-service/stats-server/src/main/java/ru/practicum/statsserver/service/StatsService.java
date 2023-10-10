package ru.practicum.statsserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsserver.exception.ValidateException;
import ru.practicum.statsserver.model.Stats;
import ru.practicum.statsserver.model.ViewStats;
import ru.practicum.statsserver.repository.StatsRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {
    private final StatsRepositoryImpl statsRepository;

    @Transactional
    public void addHit(Stats stats) {
        log.info("Регистрация обращения к {}", stats);
        statsRepository.save(stats);
    }


    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Вывод списка обращений по параметрам start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);

        if (start.isAfter(end)) {
            log.error("Недопустимый временной промежуток.");
            throw new ValidateException("Недопустимый временной промежуток.");
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
