package ru.ptacticum.main_service.event.mapper;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.ptacticum.main_service.UnionService;
import ru.ptacticum.main_service.category.mapper.CategoryMapper;
import ru.ptacticum.main_service.category.model.Category;
import ru.ptacticum.main_service.event.dto.EventFullDto;
import ru.ptacticum.main_service.event.dto.EventNewDto;
import ru.ptacticum.main_service.event.dto.EventShortDto;
import ru.ptacticum.main_service.event.model.Event;
import ru.ptacticum.main_service.event.model.Location;
import ru.ptacticum.main_service.event.repository.EventRepository;
import ru.ptacticum.main_service.event.repository.LocationRepository;
import ru.ptacticum.main_service.request.repository.RequestRepository;
import ru.ptacticum.main_service.user.mapper.UserMapper;
import ru.ptacticum.main_service.user.model.User;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.ptacticum.main_service.utils.State.PENDING;

@Component
@AllArgsConstructor
public class EventMapper {

    private UnionService unionService;
    private EventRepository eventRepository;
    private RequestRepository requestRepository;
    private LocationRepository locationRepository;

    public Event toEvent(EventNewDto eventNewDto, Category category, Location location, User user) {
        Event event = Event.builder()
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
                .state(PENDING)
                .confirmedRequests(0L)
                .title(eventNewDto.getTitle())
                .build();
        return event;
    }

    public EventFullDto toEventFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
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
        return eventFullDto;
    }

    public EventShortDto toEventShortDto(Event event) {

        EventShortDto eventShortDto = EventShortDto.builder()
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
        return eventShortDto;
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
}