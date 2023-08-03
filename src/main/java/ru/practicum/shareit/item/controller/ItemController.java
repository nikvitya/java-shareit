package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAll(userId).stream().map(i -> ItemMapper.toItemDto(i)).collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable Integer itemId,
                          @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.update(userId, itemId, itemDto));
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text).stream()
                .map(i -> ItemMapper.toItemDto(i)).collect(Collectors.toList());
    }


}
