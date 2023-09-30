package ru.practicum.stats_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats_server.model.Stats;

public interface JpaStatsRepository extends JpaRepository<Stats, Long> {
}
