package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private int id = 1;
    private final Map<Integer, Item> items = new HashMap<>();


    @Override
    public List<Item> getAll(Integer userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public Item create(Item item) {
        item.setId(idGenerator());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem, Item oldItem) {
        Item updatedItem = Item.builder()
                .id(newItem.getId())
                .name(newItem.getName() == null ? oldItem.getName() : newItem.getName())
                .description(newItem.getDescription() == null ? oldItem.getDescription() : newItem.getDescription())
                .available(newItem.getAvailable() == null ? oldItem.getAvailable() : newItem.getAvailable())
                .owner(oldItem.getOwner())
                .request(oldItem.getRequest())
                .build();
        items.put(newItem.getId(), updatedItem);
        return updatedItem;
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && i.getAvailable())
                .collect(Collectors.toList());
    }

    private int idGenerator() {
        return id++;
    }
}
