package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAll(Integer userId);

    ItemDto getItemById(Integer itemId);

    ItemDto create(Integer userId, ItemDto itemDto);

    ItemDto update(Integer userId,Integer itemId, ItemDto itemDto);

    List<ItemDto> search(String text);
}
