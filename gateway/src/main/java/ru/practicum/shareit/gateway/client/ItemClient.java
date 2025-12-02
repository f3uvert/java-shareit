package ru.practicum.shareit.gateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.dto.*;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemClient {
    private final WebClient webClient;

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        log.debug("Calling server to create item for user {}", ownerId);

        Map<String, Object> requestBody = Map.of(
                "name", itemDto.getName(),
                "description", itemDto.getDescription(),
                "available", itemDto.getAvailable(),
                "requestId", itemDto.getRequestId()
        );

        return webClient.post()
                .uri("/items")
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(ItemDto.class)
                .block();
    }

    public ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        log.debug("Calling server to update item {}", itemId);

        Map<String, Object> requestBody = Map.of(
                "name", itemUpdateDto.getName(),
                "description", itemUpdateDto.getDescription(),
                "available", itemUpdateDto.getAvailable(),
                "requestId", itemUpdateDto.getRequestId()
        );

        return webClient.patch()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(ItemDto.class)
                .block();
    }

    public ItemWithBookingsDto getItemById(Long itemId, Long userId) {
        log.debug("Calling server to get item {}", itemId);

        return webClient.get()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(ItemWithBookingsDto.class)
                .block();
    }

    public List<ItemWithBookingsDto> getItemsByOwner(Long ownerId, int from, int size) {
        log.debug("Calling server to get items for owner {}", ownerId);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(new ParameterizedTypeReference<List<ItemWithBookingsDto>>() {})
                .block();
    }

    public List<ItemDto> searchItems(String text, int from, int size) {
        log.debug("Calling server to search items with text '{}'", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/items/search")
                        .queryParam("text", text)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(new ParameterizedTypeReference<List<ItemDto>>() {})
                .block();
    }

    public CommentResponseDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        log.debug("Calling server to add comment to item {}", itemId);

        Map<String, Object> requestBody = Map.of(
                "text", commentDto.getText()
        );

        return webClient.post()
                .uri("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", String.valueOf(authorId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(CommentResponseDto.class)
                .block();
    }
}