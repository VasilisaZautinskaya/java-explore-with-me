package ru.practicum.main_service.complitation.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main_service.complitation.dto.CompilationDto;
import ru.practicum.main_service.complitation.dto.CompilationNewDto;
import ru.practicum.main_service.complitation.dto.CompilationUpdateDto;
import ru.practicum.main_service.complitation.model.Compilation;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.mapper.EventMapper;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    public CompilationDto toCompilationDto(Compilation compilation) {

        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(compilation.getEvents());

        Set<EventShortDto> eventShortDtoSet = new HashSet<>();
        for (EventShortDto shortDto : eventShortDtoList) {
            eventShortDtoSet.add(shortDto);
        }
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(eventShortDtoSet)
                .build();
    }

    public Compilation toCompilation(CompilationNewDto compilationNewDto) {
        List<Event> eventList = eventRepository.findByIdIn(compilationNewDto.getEvents());
        return Compilation.builder()
                .title(compilationNewDto.getTitle())
                .events(eventList)
                .pinned(compilationNewDto.getPinned())
                .build();
    }


    public List<CompilationDto> toCompilationDtoList(Iterable<Compilation> compilations) {

        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : compilations) {
            result.add(toCompilationDto(compilation));
        }
        return result;
    }

    public Compilation toCompilationUpdate(CompilationUpdateDto compilationUpdateDto) {
        List<Event> eventList = eventRepository.findByIdIn(compilationUpdateDto.getEvents());
        return Compilation.builder()
                .title(compilationUpdateDto.getTitle())
                .pinned(compilationUpdateDto.getPinned())
                .events(eventList)
                .build();

    }
}
