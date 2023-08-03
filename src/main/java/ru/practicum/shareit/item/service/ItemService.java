package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getAll(Integer userId);

    Item getItemById(Integer itemId);

    Item create(Integer userId, ItemDto itemDto);

    Item update(Integer userId,Integer itemId, ItemDto itemDto);

    List<Item> search(String text);
}
