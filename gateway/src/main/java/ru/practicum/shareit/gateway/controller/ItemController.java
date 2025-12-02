package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.ItemClient;
import ru.practicum.shareit.gateway.dto.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public Object createItem(@Valid @RequestBody ItemDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Gateway: POST /items | User-ID: {}", ownerId);
        return itemClient.createItem(itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") Long authorId) {
        log.info("Gateway: POST /items/{}/comment by user {}", itemId, authorId);

        ResponseEntity<Object> response = itemClient.addComment(itemId, commentDto, authorId);
        log.info("Gateway: Comment response status: {}", response.getStatusCode());
        return response;
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(@PathVariable Long itemId,
                             @Valid @RequestBody ItemUpdateDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Gateway: PATCH /items/{}", itemId);
        return itemClient.updateItem(itemId, itemDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public Object getItemById(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: GET /items/{}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public Object getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Gateway: GET /items | Owner-ID: {}", ownerId);
        return itemClient.getItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public Object searchItems(@RequestParam String text,
                              @RequestParam(defaultValue = "0") int from,
                              @RequestParam(defaultValue = "10") int size) {
        log.info("Gateway: GET /items/search | Text: '{}'", text);
        return itemClient.searchItems(text, from, size);
    }

    private String getUpdatedFields(ItemUpdateDto itemDto) {
        List<String> fields = new ArrayList<>();
        if (itemDto.getName() != null) fields.add("name");
        if (itemDto.getDescription() != null) fields.add("description");
        if (itemDto.getAvailable() != null) fields.add("available");
        return String.join(",", fields);
    }
}