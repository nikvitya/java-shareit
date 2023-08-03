package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncompatibleUserIdException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.validator.ItemDtoValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.exception.Constants.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;


    @Override
    public List<Item> getAll(Integer userId) {
        return itemRepository.getAll(userId);
    }

    @Override
    public Item getItemById(Integer itemId) {
        if (itemRepository.getById(itemId) == null) {
            throw new NotFoundException(String.format(ITEM_NOT_FOUND,itemId));
        }

        return itemRepository.getById(itemId);
    }

    @Override
    public Item create(Integer userId, ItemDto itemDto) {
        ItemDtoValidator.validateItemDto(itemDto);
        User user = userService.getUserById(userId);

        Item item = ItemMapper.toItem(user, itemDto);
        return itemRepository.create(item);
    }

    @Override
    public Item update(Integer userId, Integer itemId, ItemDto itemDto) {

        User user = userService.getUserById(userId);

        if (itemDto.getId() == null)
            itemDto.setId(itemId);

        if (itemId != itemDto.getId())
            throw new NotFoundException(String.format(ITEM_NOT_MATCH, itemDto.getId()));

        Item item = ItemMapper.toItem(user, itemDto);
        Item oldItem = getItemById(itemId);

        if (!item.getOwner().getId().equals(oldItem.getOwner().getId()))
            throw new IncompatibleUserIdException("id пользователей не совпадают.");

        return itemRepository.update(item,oldItem);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }
}
