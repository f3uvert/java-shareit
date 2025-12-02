package ru.practicum.shareit.gateway.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.CommentDto;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.dto.ItemUpdateDto;

import java.util.List;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    public ItemClient(RestTemplate rest) {
        super(rest);
    }

    public Object createItem(ItemDto itemDto, Long ownerId) {
        String path = "/items";
        ResponseEntity<Object> response = post(path, ownerId, itemDto);
        return response.getBody();
    }

    public Object updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        String path = "/items/{itemId}";
        Map<String, Object> parameters = Map.of("itemId", itemId);
        ResponseEntity<Object> response = patch(path, ownerId, parameters, itemUpdateDto);
        return response.getBody();
    }

    public Object getItemById(Long itemId, Long userId) {
        String path = "/items/{itemId}";
        Map<String, Object> parameters = Map.of("itemId", itemId);
        ResponseEntity<Object> response = get(path, userId, parameters);
        return response.getBody();
    }

    public Object getItemsByOwner(Long ownerId, int from, int size) {
        String path = "/items?from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        ResponseEntity<Object> response = get(path, ownerId, parameters);
        return response.getBody();
    }

    public Object searchItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String path = "/items/search?text={text}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        ResponseEntity<Object> response = get(path, parameters.size());
        return response.getBody();
    }

    public Object addComment(Long itemId, CommentDto commentDto, Long authorId) {
        String path = "/items/{itemId}/comment";
        Map<String, Object> parameters = Map.of("itemId", itemId);
        ResponseEntity<Object> response = post(path, authorId, parameters, commentDto);
        return response.getBody();
    }
}