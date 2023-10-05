package ru.ptacticum.main_service.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ptacticum.main_service.event.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}