package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader(USER_ID_HEADER) long ownerId,
                                           @RequestBody @Valid CreateItemRequest createItemRequest) {
        return itemClient.saveItem(ownerId, createItemRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID_HEADER) long ownerId,
                                         @PathVariable long itemId,
                                         @RequestBody @Valid UpdateItemRequest updateItemRequest) {
        return itemClient.update(ownerId, itemId, updateItemRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable long itemId,
                                           @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemClient.findDtoById(itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> findByOwnerId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return itemClient.findByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItemsByText(@RequestParam String text,
                                                             @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return itemClient.searchAvailableItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(USER_ID_HEADER) Long authorId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid CreateCommentRequest createCommentRequest) {
        return itemClient.saveComment(authorId, itemId, createCommentRequest);
    }
}