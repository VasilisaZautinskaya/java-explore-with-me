package ru.practicum.main_service.complitation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.UnionService;
import ru.practicum.main_service.complitation.dto.CompilationDto;
import ru.practicum.main_service.complitation.dto.CompilationUpdateDto;
import ru.practicum.main_service.complitation.mapper.CompilationMapper;
import ru.practicum.main_service.complitation.repository.CompilationRepository;
import ru.practicum.main_service.complitation.model.Compilation;
import ru.practicum.main_service.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final UnionService unionService;

    private final CompilationMapper compilationMapper;

    @Transactional
    public Compilation addCompilation(Compilation compilation) {

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilation.getEvents() == null || compilation.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptyList());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(compilation.getEventsIds()));
        }

        compilation = compilationRepository.save(compilation);
        return compilation;
    }

    @Transactional
    public void deleteCompilation(Long compId) {

        unionService.getCompilationOrNotFound(compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilationUpdateDto) {

        Compilation compilation = unionService.getCompilationOrNotFound(compId);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        if (compilationUpdateDto.getEvents() == null || compilationUpdateDto.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptyList());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(compilationUpdateDto.getEvents()));
        }

        if (compilationUpdateDto.getTitle() != null) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }

        compilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(compilation);
    }

    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned) {
            compilations = compilationRepository.findByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        }
        return new ArrayList<>(compilations);
    }

    public Compilation getCompilationById(Long compId) {
        return unionService.getCompilationOrNotFound(compId);
    }

}
