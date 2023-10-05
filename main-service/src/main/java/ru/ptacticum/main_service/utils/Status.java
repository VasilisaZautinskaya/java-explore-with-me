package ru.ptacticum.main_service.utils;

import ru.ptacticum.main_service.exception.ValidationException;

public enum Status {

    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED;

    public static Status getStatusValue(String status) {
        try {
            return Status.valueOf(status);
        } catch (Exception e) {
            throw new ValidationException("Unknown status: " + status);
        }
    }
}