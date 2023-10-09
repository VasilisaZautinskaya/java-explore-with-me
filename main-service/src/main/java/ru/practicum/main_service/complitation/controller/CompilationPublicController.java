package ru.practicum.main_service.complitation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.complitation.dto.CompilationDto;
import ru.practicum.main_service.complitation.mapper.CompilationMapper;
import ru.practicum.main_service.complitation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {

    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false", name = "pinned") Boolean pinned,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Получение спска всех событий с параметрами pinned = {}, and from = {}, size = {}", pinned, from, size);
        return compilationMapper.toCompilationDtoList(compilationService.getCompilations(pinned, from, size));
    }

    @GetMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable Long compId) {

        log.info("Получение списка событий по айди {}", compId);
        return compilationMapper.toCompilationDto(compilationService.getCompilationById(compId));
    }
}
