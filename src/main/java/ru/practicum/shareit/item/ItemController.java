package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public CreateItemResponse saveItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody @Valid CreateItemRequest createItemRequest) {
        return ItemMapper.toCreateItemResponse(
                itemService.saveItem(ItemMapper.toItem(userService.findById(userId), createItemRequest))
        );
    }

    @PatchMapping("/{itemId}")
    public CreateItemResponse update(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long itemId,
                                     @RequestBody CreateItemRequest createItemRequest) {
        return ItemMapper.toCreateItemResponse(itemService.update(
                ItemMapper.toItem(userService.findById(userId), createItemRequest).setId(itemId)
        ));
    }

    @GetMapping("/{itemId}")
    public GetItemResponse findById(@PathVariable long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.findDtoById(itemId, ownerId);
    }

    @GetMapping
    public List<GetItemResponse> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<SearchItemResponse> searchAvailableItemsByText(@RequestParam String text) {
        return itemService.searchAvailableItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponse saveComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable Long itemId,
                                       @RequestBody @Valid CreateCommentRequest createCommentRequest) {
        return itemService.saveComment(authorId, itemId, createCommentRequest);
    }
}