package ru.practicum.stats_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ViewStatsDto {
    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotNull
    private Long hits;
}
