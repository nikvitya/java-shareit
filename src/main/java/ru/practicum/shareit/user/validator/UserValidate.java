package ru.practicum.shareit.user.validator;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.exception.Constants.ID_NOT_POSITIVE;

@Slf4j
@Data
public class UserValidate {

    public static void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым или содержать пробелы");
        }
    }

    public static void validateId(Integer id) {
        if (id < 0) {
            throw new NotFoundException(ID_NOT_POSITIVE);
        }
    }
}
