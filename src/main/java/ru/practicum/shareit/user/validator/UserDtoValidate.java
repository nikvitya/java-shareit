package ru.practicum.shareit.user.validator;

import lombok.Data;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.exception.Constants.ID_NOT_POSITIVE;

@Data
public class UserDtoValidate {
    public static void validateUserDto(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !(userDto.getEmail().contains("@"))) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (userDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым или содержать пробелы");
        }
    }

    public static void validateDtoId(Integer id) {
        if (id < 0) {
            throw new NotFoundException(ID_NOT_POSITIVE);
        }
    }
}
