package ru.practicum.shareit.gateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.*;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemClient {
    private final WebClient webClient;
    private final DtoConverter converter;

    public ItemClient(@Value("${shareit.server.url}") String serverUrl, DtoConverter converter) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.converter = converter;
    }

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        log.debug("Calling server to create item for user {}", ownerId);

        return converter.toGatewayItemDto(
                webClient.post()
                        .uri("/items")
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(converter.toServerItemDto(itemDto))
                        .retrieve()
                        .bodyToMono(ru.practicum.shareit.server.item.dto.ItemDto.class)
                        .block()
        );
    }

    public ItemDto updateItem(Long itemId, ItemUpdateDto itemUpdateDto, Long ownerId) {
        log.debug("Calling server to update item {}", itemId);

        ru.practicum.shareit.server.item.dto.ItemDto serverDto =
                new ru.practicum.shareit.server.item.dto.ItemDto(
                        itemId,
                        itemUpdateDto.getName(),
                        itemUpdateDto.getDescription(),
                        itemUpdateDto.getAvailable(),
                        null
                );

        return converter.toGatewayItemDto(
                webClient.patch()
                        .uri("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", String.valueOf(ownerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(serverDto)
                        .retrieve()
                        .bodyToMono(ru.practicum.shareit.server.item.dto.ItemDto.class)
                        .block()
        );
    }

    public ItemWithBookingsDto getItemById(Long itemId, Long userId) {
        log.debug("Calling server to get item {}", itemId);

        return webClient.get()
                .uri("/items/{itemId}", itemId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
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
                .bodyToMono(new ParameterizedTypeReference<List<ItemWithBookingsDto>>() {})
                .block();
    }

    public List<ItemDto> searchItems(String text, int from, int size) {
        log.debug("Calling server to search items with text '{}'", text);

        List<ru.practicum.shareit.server.item.dto.ItemDto> serverItems = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/items/search")
                        .queryParam("text", text)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ru.practicum.shareit.server.item.dto.ItemDto>>() {})
                .block();

        return serverItems.stream()
                .map(converter::toGatewayItemDto)
                .collect(java.util.stream.Collectors.toList());
    }

    public CommentResponseDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        log.debug("Calling server to add comment to item {}", itemId);

        return webClient.post()
                .uri("/items/{itemId}/comment", itemId)
                .header("X-Sharer-User-Id", String.valueOf(authorId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(converter.toServerCommentDto(commentDto))
                .retrieve()
                .bodyToMono(CommentResponseDto.class)
                .block();
    }
}