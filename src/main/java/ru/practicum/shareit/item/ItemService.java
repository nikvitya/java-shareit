package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item saveItem(Item item);

    Item update(Item item);

    Item findById(long itemId);

    GetItemResponse findDtoById(long itemId, long ownerId);

    List<GetItemResponse> findByOwnerId(long userId);

    List<SearchItemResponse> searchAvailableItemsByText(String text);

    CommentResponse saveComment(Long authorId, Long itemId, CreateCommentRequest createCommentRequest);
}
