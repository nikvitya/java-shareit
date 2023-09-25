package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentRequest;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new).build()
        );
    }

    public ResponseEntity<Object> saveItem(long ownerId, CreateItemRequest createItemRequest) {
        return post("", ownerId, createItemRequest);
    }

    public ResponseEntity<Object> update(long ownerId, long itemId, UpdateItemRequest updateItemRequest) {
        return patch("/" + itemId, ownerId, updateItemRequest);
    }

    public ResponseEntity<Object> findDtoById(long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> findByOwnerId(long ownerId, Long from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> searchAvailableItemsByText(String text, Long from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> saveComment(Long authorId, Long itemId, CreateCommentRequest createCommentRequest) {
        return post("/" + itemId + "/comment", authorId, createCommentRequest);
    }
}