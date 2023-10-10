package ru.practicum.mainservice.utils;

import ru.practicum.mainservice.exception.ValidationException;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static State getStateValue(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}