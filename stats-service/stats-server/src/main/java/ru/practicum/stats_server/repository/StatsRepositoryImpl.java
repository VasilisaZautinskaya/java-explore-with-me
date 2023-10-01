package ru.practicum.stats_server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.model.ViewStats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Primary
public class StatsRepositoryImpl implements StatsRepository {
    @Autowired
    JpaStatsRepository jpaStatsRepository;
    private final JdbcTemplate jdbcTemplate;

    public StatsRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ViewStats> getAllStats() {
        String sqlQuery = "SELECT s.app, s.uri, COUNT(s.ip) FROM stats AS s GROUP BY s.app, s.uri";
        return jdbcTemplate.query(sqlQuery, this::mapRowToStats);
    }


    private ViewStats mapRowToStats(ResultSet resultSet, int rowNum) throws SQLException {
        return ViewStats.builder()
                .uri(resultSet.getString("uri"))
                .app(resultSet.getString("app"))
                .build();

    }


    @Override
    public Stats save(Stats stats) {
        return jpaStatsRepository.save(stats);

    }

    @Override
    public List<ViewStats> getAllStatsIp(LocalDateTime start, LocalDateTime end) {
        String sqlQuery = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip) FROM stats AS s GROUP BY s.app, s.uri ORDER BY COUNT(DISTINCT s.ip) DESC";
        return jdbcTemplate.query(sqlQuery, this::mapRowToStats);
    }

    @Override
    public List<ViewStats> getStatsByUrisIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        String sqlQuery = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip) FROM Stats AS s AND s.uri NOTNULL GROUP BY s.app, s.uri ORDER BY COUNT(DISTINCT s.ip) DESC ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToStats);

    }

    @Override
    public List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris) {
        String sqlQuery = "SELECT COUNT(*) FROM stats AS s WHERE s.uri IS NOT NULL";

        return jdbcTemplate.query(sqlQuery, this::mapRowToStats);
    }
}
