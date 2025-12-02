package ru.practicum.shareit.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.CommentDto;
import ru.practicum.shareit.gateway.dto.ItemDto;
import ru.practicum.shareit.gateway.dto.ItemUpdateDto;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {

    private final DtoConverter dtoConverter;

    public ItemClient(RestTemplate rest, DtoConverter dtoConverter) {
        super(rest);
        this.dtoConverter = dtoConverter;
    }

    public Object createItem(ItemDto itemDto, Long ownerId) {
        String path = "/items";
        Map<String, Object> requestBody = dtoConverter.toServerItemDto(itemDto);
        log.debug("Creating item: {} for owner {}", itemDto.getName(), ownerId);
        return post(path, ownerId, requestBody).getBody();
    }

    public Object updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        String path = "/items/{itemId}";
        Map<String, Object> parameters = Map.of("itemId", itemId);
        Map<String, Object> requestBody = dtoConverter.toServerItemUpdateDto(itemUpdateDto);
        log.debug("Sending item update request for item {}: {}", itemId, requestBody);
        return patch(path, ownerId, parameters, requestBody).getBody();
    }

    public Object getItemById(Long itemId, Long userId) {
        String path = "/items/{itemId}";
        Map<String, Object> parameters = Map.of("itemId", itemId);
        log.debug("Getting item {} for user {}", itemId, userId);
        return get(path, userId, parameters).getBody();
    }

    @SuppressWarnings("unchecked")
    public Object getItemsByOwner(Long ownerId, int from, int size) {
        String path = "/items?from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.debug("Getting items for owner {} from {} size {}", ownerId, from, size);
        ResponseEntity<Object> response = get(path, ownerId, parameters);

        if (response.getBody() instanceof List) {
            try {
                List<Map<String, Object>> itemsList = (List<Map<String, Object>>) response.getBody();
                return dtoConverter.toGatewayItemWithBookingsDtoList(itemsList);
            } catch (ClassCastException e) {
                log.error("Error casting response to List<Map<String, Object>>", e);
                return response.getBody();
            }
        }

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
        log.debug("Searching items with text '{}' from {} size {}", text, from, size);
        return get(path, null, parameters).getBody();
    }

    public ResponseEntity<Object> addComment(Long itemId, CommentDto commentDto, Long authorId) {
        String path = "/items/{itemId}/comment";
        Map<String, Object> parameters = Map.of("itemId", itemId);
        Map<String, Object> requestBody = dtoConverter.toServerCommentDto(commentDto);
        log.debug("Adding comment to item {} by user {}", itemId, authorId);
        return post(path, authorId, parameters, requestBody);
    }
}