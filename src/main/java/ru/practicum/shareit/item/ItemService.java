package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemService {

    ItemResponse saveItem(long ownerId, CreateItemRequest createItemRequest);

    ItemResponse update(long ownerId, long itemId, UpdateItemRequest updateItemRequest);

    Item findById(long itemId);

    GetItemResponse findDtoById(long itemId, long ownerId);

    List<GetItemResponse> findByOwnerId(long ownerId, Long from, int size);

    List<ItemResponse> searchAvailableItemsByText(String text, Long from, int size);

    CommentResponse saveComment(Long authorId, Long itemId, CreateCommentRequest createCommentRequest);
}
