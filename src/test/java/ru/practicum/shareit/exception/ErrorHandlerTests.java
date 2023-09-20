package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.exception.handler.ErrorHandler;
import ru.practicum.shareit.exception.handler.ErrorResponse;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlerTests {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleNotFoundExceptionTest() {
        NotFoundException notFoundException = new NotFoundException("Not found");
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(notFoundException);
        assertEquals("Не найден.", errorResponse.getError());
    }

    @Test
    void handleUserFoundExceptionTest() {
        UserNotFoundException userNotFoundException = new UserNotFoundException("A");
        ErrorResponse errorResponse = errorHandler.handleUserNotFoundException(userNotFoundException);
        assertEquals("Пользователь не найден.", errorResponse.getError());
    }

    @Test
    void handleItemFoundExceptionTest() {
        ItemNotFoundException itemNotFoundException= new ItemNotFoundException("B");
        ErrorResponse errorResponse = errorHandler.handleItemNotFoundException(itemNotFoundException);
        assertEquals("Предмет не найден.", errorResponse.getError());
    }

    @Test
    void handleUserNotBookedBeforeException() {
        UserNotBookedBeforeException userNotBookedBeforeException = new UserNotBookedBeforeException("C");
        ErrorResponse errorResponse = errorHandler.handleUserNotBookedBeforeException(userNotBookedBeforeException);
        assertEquals("Пользователь не использовал в прошлом вещь.", errorResponse.getError());
    }
//
//    @Test
//    void handleThrowableTest() {
//        Throwable throwable = new Throwable("Что-то пошло не так.");
//        ResponseEntity<Map<String, String>> response = errorHandler.handleThrowable(throwable);
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals(response.getBody().get("error"), "Что-то пошло не так.");
//    }
//
//    @Test
//    void handleValidationExceptionTest() {
//        ValidationException validationException = new ValidationException("Ошибка валидации.");
//        ResponseEntity<Map<String, String>> response = errorHandler
//                .handleValidationException(validationException);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals(response.getBody().get("error"), "Ошибка валидации.");
//    }
//
//    @Test
//    void handleConstraintViolationExceptionTest() {
//        ConstraintViolationException validationException = new ConstraintViolationException(new HashSet<>());
//        ResponseEntity<Map<String, String>> response = errorHandler
//                .handleConstraintViolationException(validationException);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
}