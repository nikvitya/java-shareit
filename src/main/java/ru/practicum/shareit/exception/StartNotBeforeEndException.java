package ru.practicum.shareit.exception;

public class StartNotBeforeEndException extends RuntimeException {
    public StartNotBeforeEndException(String message) {
        super(message);
    }
}