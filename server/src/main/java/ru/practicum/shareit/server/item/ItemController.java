package ru.practicum.shareit.server.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.item.dto.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("POST /items | User-ID: {} | Item: {} | RequestId: {}",
                ownerId, itemDto.getName(), itemDto.getRequestId());
        return itemService.createItem(itemDto, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@PathVariable Long itemId,
                                         @Valid @RequestBody CommentDto commentDto,
                                         @RequestHeader("X-Sharer-User-Id") Long authorId) {
        log.info("POST /items/{}/comment | User-ID: {}", itemId, authorId);

        CommentResponseDto result = itemService.addComment(itemId, commentDto, authorId);
        log.info("POST /items/{}/comment | Created comment ID: {}", itemId, result.getId());

        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @Valid @RequestBody ItemUpdateDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("PATCH /items/{} | User-ID: {} | Updates: {}",
                itemId, ownerId, getUpdatedFields(itemDto));

        ItemDto result = itemService.updateItem(itemId, itemDto, ownerId);
        log.info("PATCH /items/{} | Update successful", itemId);

        return result;
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItemById(@PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items/{} | User-ID: {}", itemId, userId);

        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items | Owner-ID: {} | From: {} | Size: {}", ownerId, from, size);

        List<ItemWithBookingsDto> result = itemService.getItemsByOwner(ownerId, from, size);
        log.info("GET /items | Found {} items for owner", result.size());

        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        log.info("GET /items/search | Text: '{}' | From: {} | Size: {}", text, from, size);

        List<ItemDto> result = itemService.searchItems(text, from, size);
        log.info("GET /items/search | Found {} items", result.size());

        return result;
    }

    private String getUpdatedFields(ItemUpdateDto itemDto) {
        List<String> fields = new ArrayList<>();
        if (itemDto.getName() != null) fields.add("name");
        if (itemDto.getDescription() != null) fields.add("description");
        if (itemDto.getAvailable() != null) fields.add("available");
        return String.join(",", fields);
    }
}