package ru.practicum.shareit.server.item;

import ru.practicum.shareit.server.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long ownerId);

    // Используем ItemUpdateDto вместо ItemDto
    ItemDto updateItem(Long itemId, ItemUpdateDto itemDto, Long ownerId);

    ItemWithBookingsDto getItemById(Long itemId, Long userId);

    List<ItemWithBookingsDto> getItemsByOwner(Long ownerId, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);

    CommentResponseDto addComment(Long itemId, CommentDto commentDto, Long authorId);
}