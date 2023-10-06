package ru.practicum.stats_server.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.practicum.stats_server.model.Stats;
import ru.practicum.stats_server.model.ViewStats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
@Primary
public class StatsRepositoryImpl implements StatsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StatsRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ViewStats> getAllStats() {
        String sqlQuery = "SELECT s.app, s.uri, COUNT(s.ip)" +
                "FROM stats AS s " +
                "GROUP BY s.app, s.uri ORDER BY COUNT(s.ip) DESC ";

        return jdbcTemplate.query(sqlQuery, this::mapRowToStats);
    }

    @Override
    public void save(Stats stats) {
        String sqlQuery = "INSERT INTO stats(app, ip, timestamp, uri)" +
                "VALUES(:app, :ip, :timestamp, :uri)";
        jdbcTemplate.update(sqlQuery, Map.of(
                "app", stats.getApp(),
                "ip", stats.getIp(),
                "timestamp", stats.getTimestamp(),
                "uri", stats.getUri())
        );


    }

    public List<ViewStats> getAllStatsDistinctIp(LocalDateTime start, LocalDateTime end) {
        String sqlQuery = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip)" +
                "FROM stats AS s " +
                "WHERE s.timestamp BETWEEN (:start) AND (:end) " +
                "GROUP BY s.app, s.uri " +
                "ORDER BY COUNT(DISTINCT s.ip) DESC";
        return jdbcTemplate.query(sqlQuery, Map.of("start", start, "end", end), this::mapRowToStats);
    }

    public List<ViewStats> getStatsByUrisDistinctIp(LocalDateTime start, LocalDateTime end, List<String> uris) {
        String sqlQuery = "SELECT s.app, s.uri, COUNT(DISTINCT s.ip) " +
                "FROM Stats AS s " +
                "WHERE s.uri IN(:uris) AND s.timestamp BETWEEN (:start) AND (:end)" +
                "GROUP BY s.app, s.uri " +
                "ORDER BY COUNT(DISTINCT s.ip) DESC ";
        return jdbcTemplate.query(sqlQuery, Map.of("uris", uris, "start", start, "end", end), this::mapRowToStats);

    }

    @Override
    public List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris) {
        String sqlQuery = "SELECT s.app, s.uri, COUNT(s.ip) " +
                "FROM stats AS s " +
                "WHERE s.uri IN(:uris) AND s.timestamp BETWEEN (:start) AND (:end)" +
                "GROUP BY s.app, s.uri " +
                "ORDER BY COUNT(s.ip) DESC";

        return jdbcTemplate.query(sqlQuery, Map.of("uris", uris, "start", start, "end", end), this::mapRowToStats);
    }

    private ViewStats mapRowToStats(ResultSet resultSet, int rowNum) throws SQLException {
        return ViewStats.builder()
                .uri(resultSet.getString("uri"))
                .app(resultSet.getString("app"))
                .hits(resultSet.getLong("count"))
                .build();

    }
}