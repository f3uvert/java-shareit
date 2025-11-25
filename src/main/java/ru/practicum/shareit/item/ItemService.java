package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto itemDto, Long ownerId);

    ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId);

    ItemWithBookingsDto getItemById(Long itemId, Long userId);

    List<ItemForOwnerDto> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItems(String text);

    CommentResponseDto addComment(Long itemId, CommentDto commentDto, Long authorId);

}