package ru.ptacticum.main_service.complitation.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.ptacticum.main_service.UnionService;
import ru.ptacticum.main_service.complitation.model.Compilation;
import ru.ptacticum.main_service.complitation.repository.CompilationRepository;
import ru.ptacticum.main_service.event.model.Event;
import ru.ptacticum.main_service.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final UnionService unionService;

    public Compilation addCompilation(Compilation compilation) {

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        if (compilation.getEvents() == null || compilation.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(compilation.getEventsIds()));
        }

        compilation = compilationRepository.save(compilation);
        return compilation;
    }

    public void deleteCompilation(Long compId) {

        unionService.getCompilationOrNotFound(compId);
        compilationRepository.deleteById(compId);
    }

    public Compilation updateCompilation(Long compId, Compilation compilation) {

        Compilation compilation1 = unionService.getCompilationOrNotFound(compId);

        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        if (compilation.getEvents() == null || compilation.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(compilation.getEventsIds()));
        }

        if (compilation.getTitle() != null) {
            compilation.setTitle(compilation.getTitle());
        }

        compilation = compilationRepository.save(compilation);
        return compilation;

    }

    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned) {
            compilations = compilationRepository.findByPinned(pinned, pageRequest);
        } else {
            compilations = compilationRepository.findAll(pageRequest).getContent();
            ;
        }
        return new ArrayList<>(compilations);
    }

    public Compilation getCompilationById(Long compId) {

        Compilation compilation = unionService.getCompilationOrNotFound(compId);

        return compilation;
    }
}
