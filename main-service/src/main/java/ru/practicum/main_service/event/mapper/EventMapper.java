package ru.practicum.main_service.event.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main_service.UnionService;
import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.event.dto.EventUpdateDto;
import ru.practicum.main_service.event.repository.LocationRepository;
import ru.practicum.main_service.utils.State;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.EventNewDto;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Location;
import ru.practicum.main_service.user.mapper.UserMapper;
import ru.practicum.main_service.user.model.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class EventMapper {

    private UnionService unionService;
    private LocationRepository locationRepository;

    public Event toEvent(EventNewDto eventNewDto, Category category, Location location, User user) {
        return Event.builder()
                .annotation(eventNewDto.getAnnotation())
                .category(category)
                .description(eventNewDto.getDescription())
                .eventDate(eventNewDto.getEventDate())
                .initiator(user)
                .location(location)
                .paid(eventNewDto.getPaid())
                .participantLimit(eventNewDto.getParticipantLimit())
                .requestModeration(eventNewDto.getRequestModeration())
                .createdOn(LocalDateTime.now())
                .views(0L)
                .state(State.PENDING)
                .confirmedRequests(0L)
                .title(eventNewDto.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {

        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public List<EventFullDto> toEventFullDtoList(Iterable<Event> events) {
        List<EventFullDto> result = new ArrayList<>();

        for (Event event : events) {
            result.add(toEventFullDto(event));
        }
        return result;
    }

    public List<EventShortDto> toEventShortDtoList(Iterable<Event> events) {
        List<EventShortDto> result = new ArrayList<>();

        for (Event event : events) {
            result.add(toEventShortDto(event));
        }
        return result;
    }

    public Event fromNewDto(EventNewDto eventNewDto, Long userId) {
        User user = unionService.getUserOrNotFound(userId);
        Category category = unionService.getCategoryOrNotFound(eventNewDto.getCategory());
        Location location = locationRepository.save(LocationMapper.toLocation(eventNewDto.getLocation()));
        Event event = toEvent(eventNewDto, category, location, user);
        return event;
    }

    public Event toEventUpdate(EventUpdateDto eventUpdateDto, Long eventId) {
        Category category = eventUpdateDto.getCategory() != null
                ? unionService.getCategoryOrNotFound(eventUpdateDto.getCategory())
                : null;
        Location location = eventUpdateDto.getLocation() != null
                ? locationRepository.save(LocationMapper.toLocation(eventUpdateDto.getLocation()))
                : null;
        return Event.builder()
                .id(eventId)
                .annotation(eventUpdateDto.getAnnotation())
                .category(category)
                .description(eventUpdateDto.getDescription())
                .eventDate(eventUpdateDto.getEventDate())
                .location(location)
                .paid(eventUpdateDto.getPaid())
                .participantLimit(eventUpdateDto.getParticipantLimit())
                .requestModeration(eventUpdateDto.getRequestModeration())
                .title(eventUpdateDto.getTitle())
                .build();
    }

}