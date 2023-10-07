package ru.practicum.main_service.complitation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.complitation.dto.CompilationDto;
import ru.practicum.main_service.complitation.dto.CompilationNewDto;
import ru.practicum.main_service.complitation.dto.CompilationUpdateDto;
import ru.practicum.main_service.complitation.mapper.CompilationMapper;
import ru.practicum.main_service.complitation.model.Compilation;
import ru.practicum.main_service.complitation.service.CompilationService;

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

        log.info("Delete Compilation {} ", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDto updateCompilation(@Valid @RequestBody CompilationUpdateDto compilationUpdateDto,
                                            @PathVariable Long compId) {

        log.info("Update Compilation {} ", compId);
        Compilation compilation = compilationMapper.toCompilaytionUpdate(compilationUpdateDto);
        return compilationMapper.toCompilationDto(compilationService.updateCompilation(compId, compilation));
    }
}
