package ru.practicum.mainservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.mainservice.event.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}