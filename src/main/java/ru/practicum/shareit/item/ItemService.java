package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    CreateItemResponse saveItem(long userId, CreateItemRequest createItemRequest);

    CreateItemResponse update(long userId, long itemId, CreateItemRequest createItemRequest);

    Item findById(long itemId);

    GetItemResponse findDtoById(long itemId, long ownerId);

    List<GetItemResponse> findByOwnerId(long userId);

    List<SearchItemResponse> searchAvailableItemsByText(String text);

    CommentResponse saveComment(Long authorId, Long itemId, CreateCommentRequest createCommentRequest);
}
