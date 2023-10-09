package ru.practicum.mainservice.utils;

import ru.practicum.mainservice.exception.ValidationException;

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