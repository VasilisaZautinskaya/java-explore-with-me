package ru.ptacticum.main_service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ptacticum.main_service.request.model.Request;
import ru.ptacticum.main_service.utils.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getByRequesterId(Long userId);

    List<Request> getByEventId(Long eventId);

    Optional<Request> getByRequesterIdAndEventId(Long userId, Long eventId);

    Long countAllByEventIdAndStatus(Long eventId, Status status);
}