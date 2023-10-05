package ru.ptacticum.main_service.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ptacticum.main_service.event.repository.EventRepository;
import ru.ptacticum.main_service.request.repository.RequestRepository;

@Slf4j
@Service
@AllArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
}
