package ru.practicum.shareit.exception;

public class IncompatibleUserIdException extends RuntimeException {
    public IncompatibleUserIdException(String message) {
        super(message);
    }
}
