package ru.practicum.shareit.exception;

public class IncompatibleItemIdException extends RuntimeException {
    public IncompatibleItemIdException(String message) {
        super(message);
    }
}