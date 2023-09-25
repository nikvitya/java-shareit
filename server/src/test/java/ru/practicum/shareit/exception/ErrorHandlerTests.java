package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.exception.handler.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTests {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleUserNotBookedBeforeException() {
        UserNotBookedBeforeException userNotBookedBeforeException = new UserNotBookedBeforeException("C");
        ErrorResponse errorResponse = errorHandler.handleUserNotBookedBeforeException(userNotBookedBeforeException);
        assertEquals("Пользователь не использовал в прошлом вещь.", errorResponse.getError());
    }

    @Test
    void handleStatusAlreadyChangedExceptionTest() {
        StatusAlreadyChangedException statusAlreadyChangedException = new StatusAlreadyChangedException("n");
        ErrorResponse errorResponse = errorHandler.handleStatusAlreadyChangedException(statusAlreadyChangedException);
        assertEquals("Статус был изменён ранее.", errorResponse.getError());
    }

    @Test
    void handleWrongUserIdExceptionTest() {
        WrongUserIdException wrongUserIdException = new WrongUserIdException("n");
        ErrorResponse errorResponse = errorHandler.handleWrongUserIdException(wrongUserIdException);
        assertEquals("Запрещенный id пользователя.", errorResponse.getError());
    }

    @Test
    void handleUnknownStateExceptionTest() {
        UnknownStateException unknownStateException = new UnknownStateException("n");
        ErrorResponse errorResponse = errorHandler.handleUnknownStateException(unknownStateException);
        assertEquals("Unknown state: UNSUPPORTED_STATUS", errorResponse.getError());
    }

    @Test
    void handleBookingNotFoundExceptionTest() {
        BookingNotFoundException bookingNotFoundException = new BookingNotFoundException("n");
        ErrorResponse errorResponse = errorHandler.handleBookingNotFoundException(bookingNotFoundException);
        assertEquals("Бронь не найдена.", errorResponse.getError());
    }

    @Test
    void handleStartAfterEndExceptionTest() {
        StartNotBeforeEndException startNotBeforeEndException = new StartNotBeforeEndException("n");
        ErrorResponse errorResponse = errorHandler.handleStartAfterEndException(startNotBeforeEndException);
        assertEquals("Начало должно быть раньше окончания.", errorResponse.getError());
    }

    @Test
    void handleItemNotAvailableExceptionTest() {
        ItemNotAvailableException itemNotAvailableException = new ItemNotAvailableException("n");
        ErrorResponse errorResponse = errorHandler.handleItemNotAvailableException(itemNotAvailableException);
        assertEquals("Предмет не доступен.", errorResponse.getError());
    }

    @Test
    void handleIncompatibleItemIdExceptionTest() {
        IncompatibleItemIdException incompatibleItemIdException = new IncompatibleItemIdException("n");
        ErrorResponse errorResponse = errorHandler.handleIncompatibleItemIdException(incompatibleItemIdException);
        assertEquals("id предметов не совпадают.", errorResponse.getError());
    }

    @Test
    void handleEmailAlreadyExistExceptionTest() {
        EmailAlreadyExistException emailAlreadyExistException = new EmailAlreadyExistException("/ ");
        ErrorResponse errorResponse = errorHandler.handleEmailAlreadyExistException(emailAlreadyExistException);
        assertEquals("Еmail уже существует.", errorResponse.getError());
    }

    @Test
    void handleNotFoundExceptionTest() {
        NotFoundException notFoundException = new NotFoundException("Not found");
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(notFoundException);
        assertEquals("Не найден.", errorResponse.getError());
    }

    @Test
    void handleUserNotFoundExceptionTest() {
        UserNotFoundException userNotFoundException = new UserNotFoundException("A");
        ErrorResponse errorResponse = errorHandler.handleUserNotFoundException(userNotFoundException);
        assertEquals("Пользователь не найден.", errorResponse.getError());
    }

    @Test
    void handleItemNotFoundExceptionTest() {
        ItemNotFoundException itemNotFoundException = new ItemNotFoundException("B");
        ErrorResponse errorResponse = errorHandler.handleItemNotFoundException(itemNotFoundException);
        assertEquals("Предмет не найден.", errorResponse.getError());
    }

    @Test
    void handleExceptionTest() {
        Exception exception = new Exception("C");
        ErrorResponse errorResponse = errorHandler.handleException(exception);
        assertEquals("Произошла непредвиденная ошибка.", errorResponse.getError());
    }
}