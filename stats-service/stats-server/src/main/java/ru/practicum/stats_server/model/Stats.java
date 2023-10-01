package ru.practicum.stats_server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "stats", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "app_name")
    @NotNull
    String app;

    @NotNull
    String uri;

    @Column(name = "user_ip")
    @Size(max = 15)
    @NotNull
    String ip;

    @Column(name = "created")
    @NotNull
    LocalDateTime timestamp;
}