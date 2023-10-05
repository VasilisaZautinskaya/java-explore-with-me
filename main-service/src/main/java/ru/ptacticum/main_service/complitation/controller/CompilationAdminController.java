package ru.ptacticum.main_service.complitation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ptacticum.main_service.complitation.dto.CompilationDto;
import ru.ptacticum.main_service.complitation.mapper.CompilationMapper;
import ru.ptacticum.main_service.complitation.model.Compilation;
import ru.ptacticum.main_service.complitation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody Compilation compilation) {

        log.info("Add Compilation {} ", compilation.getTitle());
        return CompilationMapper.toCompilationDto(compilationService.addCompilation(compilation));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {

        log.info("Delete Compilation {} ", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDto updateCompilation(@Valid @RequestBody Compilation compilation,
                                            @PathVariable Long compId) {

        log.info("Update Compilation {} ", compId);
        return CompilationMapper.toCompilationDto(compilationService.updateCompilation(compId, compilation));
    }
}
