package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.exception.Constants.*;

public class ItemDtoValidator {
    public static void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException(ITEM_NAME_IS_BLANK_OR_NULL);
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException(ITEM_DESCRIPTION_IS_BLANK_OR_NULL);
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException(ITEM_AVAILABILITY_WRITTEN_WRONG);
        }
    }


    public static void validateId(Integer id) {
        if (id < 0) {
            throw new NotFoundException(ID_NOT_POSITIVE);
        }
    }
}
