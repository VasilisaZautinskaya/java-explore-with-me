package ru.practicum.main_service.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main_service.event.dto.LocationDto;
import ru.practicum.main_service.event.model.Location;

@UtilityClass
public class LocationMapper {

    public LocationDto toLocationDto(Location location) {
        LocationDto locationDto = LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
        return locationDto;
    }

    public Location toLocation(LocationDto locationDto) {
        Location location = Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
        return location;
    }
}