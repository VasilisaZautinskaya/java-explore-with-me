package ru.ptacticum.main_service.complitation.mapper;

import lombok.experimental.UtilityClass;
import ru.ptacticum.main_service.complitation.dto.CompilationDto;
import ru.ptacticum.main_service.complitation.dto.CompilationNewDto;
import ru.ptacticum.main_service.complitation.model.Compilation;
import ru.ptacticum.main_service.event.dto.EventShortDto;
import ru.ptacticum.main_service.event.mapper.EventMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public CompilationDto toCompilationDto(Compilation compilation) {

        List<EventShortDto> eventShortDtoList = EventMapper.toEventShortDtoList(compilation.getEvents());

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
        return Compilation.builder()
                .title(compilationNewDto.getTitle())
                .pinned(compilationNewDto.getPinned())
                .build();
    }

    public Set<CompilationDto> toCompilationDtoSet(Iterable<Compilation> compilations) {

        Set<CompilationDto> result = new HashSet<>();
        for (Compilation compilation : compilations) {
            result.add(toCompilationDto(compilation));
        }
        return result;
    }
}
