package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemResponse saveItem(@RequestHeader(USER_ID_HEADER) long ownerId,
                                 @RequestBody @Valid CreateItemRequest createItemRequest) {
        return itemService.saveItem(ownerId, createItemRequest);
    }

    @PatchMapping("/{itemId}")
    public ItemResponse update(@RequestHeader(USER_ID_HEADER) long ownerId,
                               @PathVariable long itemId,
                               @RequestBody @Valid UpdateItemRequest updateItemRequest) {
        return itemService.update(ownerId, itemId, updateItemRequest);
    }

    @GetMapping("/{itemId}")
    public GetItemResponse findById(@PathVariable long itemId,
                                    @RequestHeader(USER_ID_HEADER) Long ownerId) {
        return itemService.findDtoById(itemId, ownerId);
    }

    @GetMapping
    public List<GetItemResponse> findByOwnerId(@RequestHeader(USER_ID_HEADER) long ownerId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                               @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return itemService.findByOwnerId(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemResponse> searchAvailableItemsByText(@RequestParam String text,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                                         @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return itemService.searchAvailableItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse saveComment(@RequestHeader(USER_ID_HEADER) Long authorId, @PathVariable Long itemId,
                                       @RequestBody @Valid CreateCommentRequest createCommentRequest) {
        return itemService.saveComment(authorId, itemId, createCommentRequest);
    }
}