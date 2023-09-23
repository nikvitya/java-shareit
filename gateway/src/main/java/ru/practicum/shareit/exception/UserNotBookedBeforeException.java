package ru.practicum.shareit.exception;

public class UserNotBookedBeforeException extends RuntimeException {
    public UserNotBookedBeforeException(String message) {
        super(message);
    }
}
