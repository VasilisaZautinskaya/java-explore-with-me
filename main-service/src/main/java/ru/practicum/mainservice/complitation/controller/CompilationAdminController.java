package ru.practicum.mainservice.complitation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.complitation.dto.CompilationDto;
import ru.practicum.mainservice.complitation.dto.CompilationNewDto;
import ru.practicum.mainservice.complitation.dto.CompilationUpdateDto;
import ru.practicum.mainservice.complitation.mapper.CompilationMapper;
import ru.practicum.mainservice.complitation.model.Compilation;
import ru.practicum.mainservice.complitation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody CompilationNewDto compilationNewDto) {

        log.info("Добавление подборки событий {} ", compilationNewDto.getTitle());
        Compilation compilation = compilationMapper.toCompilation(compilationNewDto);
        return compilationMapper.toCompilationDto(compilationService.addCompilation(compilation));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {

        log.info("Удаление подборки событий с id {} ", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDto updateCompilation(@Valid @RequestBody CompilationUpdateDto compilationUpdateDto,
                                            @PathVariable Long compId) {

        log.info("Обновление подборки событий с id {} ", compId);
        return compilationService.updateCompilation(compId, compilationUpdateDto);
    }
}
