package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Available cannot be null");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + ownerId));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemDto itemDto, Long ownerId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + itemId));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("User is not the owner of this item");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingsDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + itemId));

        List<CommentResponseDto> comments = getCommentsForItem(itemId);

        if (item.getOwner().getId().equals(userId)) {
            ItemForOwnerDto itemForOwner = createItemForOwner(item, comments);
            // Конвертируем ItemForOwnerDto в ItemWithBookingsDto
            return convertToItemWithBookingsDto(itemForOwner);
        } else {
            return createItemForUser(item, comments);
        }
    }

    private ItemWithBookingsDto convertToItemWithBookingsDto(ItemForOwnerDto itemForOwner) {
        ItemWithBookingsDto.BookingInfoDto lastBooking = null;
        ItemWithBookingsDto.BookingInfoDto nextBooking = null;

        if (itemForOwner.getLastBooking() != null) {
            lastBooking = new ItemWithBookingsDto.BookingInfoDto(
                    itemForOwner.getLastBooking().getId(),
                    itemForOwner.getLastBooking().getBookerId()
            );
        }

        if (itemForOwner.getNextBooking() != null) {
            nextBooking = new ItemWithBookingsDto.BookingInfoDto(
                    itemForOwner.getNextBooking().getId(),
                    itemForOwner.getNextBooking().getBookerId()
            );
        }

        return new ItemWithBookingsDto(
                itemForOwner.getId(),
                itemForOwner.getName(),
                itemForOwner.getDescription(),
                itemForOwner.getAvailable(),
                lastBooking,
                nextBooking,
                itemForOwner.getComments(),
                itemForOwner.getRequestId()
        );
    }

    @Override
    public List<ItemForOwnerDto> getItemsByOwner(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + ownerId));

        List<Item> items = itemRepository.findByOwnerIdOrderById(ownerId);
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<CommentResponseDto>> commentsByItem = getCommentsForItems(itemIds);

        return items.stream()
                .map(item -> createItemForOwner(item, commentsByItem.getOrDefault(item.getId(), List.of())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long itemId, CommentDto commentDto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + authorId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + itemId));

        validateUserCanComment(authorId, itemId);

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return toCommentResponseDto(savedComment);
    }

    private ItemForOwnerDto createItemForOwner(Item item, List<CommentResponseDto> comments) {
        ItemForOwnerDto.BookingInfoDto lastBooking = getLastBooking(item.getId());
        ItemForOwnerDto.BookingInfoDto nextBooking = getNextBooking(item.getId());

        return new ItemForOwnerDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                comments,
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    private ItemWithBookingsDto createItemForUser(Item item, List<CommentResponseDto> comments) {
        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                comments,
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    private ItemForOwnerDto.BookingInfoDto getLastBooking(Long itemId) {
        return bookingRepository
                .findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                        itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(booking -> new ItemForOwnerDto.BookingInfoDto(
                        booking.getId(),
                        booking.getBooker().getId(),
                        booking.getBooker().getName()))
                .orElse(null);
    }

    private ItemForOwnerDto.BookingInfoDto getNextBooking(Long itemId) {
        return bookingRepository
                .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                        itemId, LocalDateTime.now(), BookingStatus.APPROVED)
                .map(booking -> new ItemForOwnerDto.BookingInfoDto(
                        booking.getId(),
                        booking.getBooker().getId(),
                        booking.getBooker().getName()))
                .orElse(null);
    }

    private List<CommentResponseDto> getCommentsForItem(Long itemId) {
        return commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(this::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    private Map<Long, List<CommentResponseDto>> getCommentsForItems(List<Long> itemIds) {
        return commentRepository.findByItemIdInOrderByCreatedDesc(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(this::toCommentResponseDto, Collectors.toList())
                ));
    }

    private void validateUserCanComment(Long userId, Long itemId) {
        boolean hasBooked = bookingRepository.hasUserBookedItem(userId, itemId, LocalDateTime.now());
        if (!hasBooked) {
            throw new ValidationException("User can only comment on items they have booked in the past");
        }
    }

    private CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }


}