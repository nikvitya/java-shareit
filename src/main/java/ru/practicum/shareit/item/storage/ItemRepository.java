package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> getAll(Integer userId);
    Item getById(Integer itemId);

    Item create(Item item);

    Item update(Item item, Item oldItem);

    List<Item> search(String text);
}
