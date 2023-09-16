package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler(UserNotBookedBeforeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotBookedBeforeException(UserNotBookedBeforeException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Пользователь не использовал в прошлом вещь.", e.getMessage());
    }

    @ExceptionHandler(StatusAlreadyChangedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStatusAlreadyChangedException(StatusAlreadyChangedException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Статус был изменён ранее.", e.getMessage());
    }

    @ExceptionHandler(WrongUserIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWrongUserIdException(WrongUserIdException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Запрещенный id пользователя.", e.getMessage());
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnknownStateException(UnknownStateException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(BookingNotFoundException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Бронь не найдена.", e.getMessage());
    }

    @ExceptionHandler(StartNotBeforeEndException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStartAfterEndException(StartNotBeforeEndException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Начало должно быть раньше окончания.", e.getMessage());
    }

    @ExceptionHandler(ItemNotAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemNotAvailableException(ItemNotAvailableException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Предмет не доступен.", e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Пользователь не найден.", e.getMessage());
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(ItemNotFoundException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Предмет не найден.", e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExistException(EmailAlreadyExistException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Еmail уже существует.", e.getMessage());
    }

    @ExceptionHandler(IncompatibleItemIdException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIncompatibleItemIdException(IncompatibleItemIdException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("id предметов не совпадают.", e.getMessage());
    }

    @ExceptionHandler(IncompatibleUserIdException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIncompatibleUserIdException(IncompatibleUserIdException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("id пользователей не совпадают.", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.warn("{}", e.getMessage());
        return new ErrorResponse("Произошла непредвиденная ошибка.", e.getMessage());
    }
}