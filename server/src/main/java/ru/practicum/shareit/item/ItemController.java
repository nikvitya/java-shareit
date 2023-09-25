package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponse saveItem(@RequestHeader(USER_ID_HEADER) long ownerId,
                                 @RequestBody CreateItemRequest createItemRequest) {
        return itemService.saveItem(ownerId, createItemRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestHeader(USER_ID_HEADER) long ownerId,
                               @PathVariable long itemId,
                               @RequestBody UpdateItemRequest updateItemRequest) {
        return itemService.update(ownerId, itemId, updateItemRequest);
    }

    @GetMapping("/{itemId}")
    public GetItemResponse findById(@PathVariable long itemId,
                                    @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.findDtoById(itemId, ownerId);
    }

    @GetMapping
    public List<GetItemResponse> findByOwnerId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                               @RequestParam(defaultValue = "0") Long from,
                                               @RequestParam(defaultValue = "10") int size) {
        return itemService.findByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchAvailableItemsByText(@RequestParam String text,
                                                         @RequestParam(defaultValue = "0") Long from,
                                                         @RequestParam(defaultValue = "10") int size) {
        return itemService.searchAvailableItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse saveComment(@RequestHeader(USER_ID_HEADER) Long authorId, @PathVariable Long itemId,
                                       @RequestBody CreateCommentRequest createCommentRequest) {
        return itemService.saveComment(authorId, itemId, createCommentRequest);
    }
}