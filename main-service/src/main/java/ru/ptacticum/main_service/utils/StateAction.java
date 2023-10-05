package ru.ptacticum.main_service.utils;


import ru.ptacticum.main_service.exception.ValidationException;

public enum StateAction {
    PUBLISH_EVENT,
    REJECT_EVENT,
    CANCEL_REVIEW,
    SEND_TO_REVIEW;

    public static StateAction getStateActionValue(String stateAction) {
        try {
            return StateAction.valueOf(stateAction);
        } catch (Exception e) {
            throw new ValidationException("Unknown stateAction: " + stateAction);
        }
    }
}