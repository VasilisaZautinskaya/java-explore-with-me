package ru.practicum.mainservice.complitation.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.complitation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    List<Compilation> findByPinned(Boolean pinned, PageRequest pageRequest);
}
