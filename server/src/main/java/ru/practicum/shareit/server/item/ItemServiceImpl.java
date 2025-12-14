package ru.practicum.shareit.server.item;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.BookingStatus;
import ru.practicum.shareit.server.item.dto.*;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        log.info("Creating item: name='{}' for ownerId={}", itemDto.getName(), ownerId);

        try {
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new NoSuchElementException("User not found with id: " + ownerId));

            if (itemDto.getName() == null || itemDto.getName().isBlank()) {
                throw new ValidationException("Item name cannot be blank");
            }

            if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
                throw new ValidationException("Item description cannot be blank");
            }

            if (itemDto.getAvailable() == null) {
                throw new ValidationException("Item availability cannot be null");
            }

            Item item = new Item();
            item.setName(itemDto.getName().trim());
            item.setDescription(itemDto.getDescription().trim());
            item.setAvailable(itemDto.getAvailable());
            item.setOwner(owner);

            // Обработка requestId если указан
            if (itemDto.getRequestId() != null) {
                ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                        .orElseThrow(() -> new NoSuchElementException(
                                "Item request not found with id: " + itemDto.getRequestId()));
                item.setRequest(request);
                log.debug("Item created for requestId: {}", itemDto.getRequestId());
            }

            Item savedItem = itemRepository.save(item);
            log.info("Item created successfully with id: {}", savedItem.getId());

            return ItemMapper.toItemDto(savedItem);

        } catch (NoSuchElementException | ValidationException e) {
            log.error("Error creating item: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating item: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create item: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, ItemUpdateDto itemDto, Long ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + itemId));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Only owner can update item");
        }

        if (itemDto.getName() == null &&
                itemDto.getDescription() == null &&
                itemDto.getAvailable() == null) {
            throw new ValidationException("At least one field must be provided for update");
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingsDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + itemId));

        ItemWithBookingsDto itemWithBookings = new ItemWithBookingsDto();
        itemWithBookings.setId(item.getId());
        itemWithBookings.setName(item.getName());
        itemWithBookings.setDescription(item.getDescription());
        itemWithBookings.setAvailable(item.getAvailable());
        itemWithBookings.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepository.findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
                            itemId, now, BookingStatus.APPROVED)
                    .ifPresent(booking -> itemWithBookings.setLastBooking(
                            new ItemWithBookingsDto.BookingInfoDto(
                                    booking.getId(),
                                    booking.getBooker().getId()
                            )
                    ));

            bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                            itemId, now, BookingStatus.APPROVED)
                    .ifPresent(booking -> itemWithBookings.setNextBooking(
                            new ItemWithBookingsDto.BookingInfoDto(
                                    booking.getId(),
                                    booking.getBooker().getId()
                            )
                    ));
        }

        List<CommentResponseDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(this::toCommentResponseDto)
                .collect(Collectors.toList());
        itemWithBookings.setComments(comments);

        return itemWithBookings;
    }

    @Override
    public List<ItemWithBookingsDto> getItemsByOwner(Long ownerId, int from, int size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + ownerId));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Item> items = itemRepository.findByOwnerId(ownerId, pageRequest);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        Map<Long, List<Booking>> bookingsByItemId = bookingRepository
                .findByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        Map<Long, List<Comment>> commentsByItemId = commentRepository
                .findByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        return items.stream()
                .map(item -> {
                    ItemWithBookingsDto dto = new ItemWithBookingsDto();
                    dto.setId(item.getId());
                    dto.setName(item.getName());
                    dto.setDescription(item.getDescription());
                    dto.setAvailable(item.getAvailable());
                    dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);

                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());
                    LocalDateTime now = LocalDateTime.now();

                    itemBookings.stream()
                            .filter(b -> b.getEnd().isBefore(now) && b.getStatus() == BookingStatus.APPROVED)
                            .max(Comparator.comparing(Booking::getEnd))
                            .ifPresent(lastBooking -> dto.setLastBooking(
                                    new ItemWithBookingsDto.BookingInfoDto(
                                            lastBooking.getId(),
                                            lastBooking.getBooker().getId()
                                    )));

                    itemBookings.stream()
                            .filter(b -> b.getStart().isAfter(now) && b.getStatus() == BookingStatus.APPROVED)
                            .min(Comparator.comparing(Booking::getStart))
                            .ifPresent(nextBooking -> dto.setNextBooking(
                                    new ItemWithBookingsDto.BookingInfoDto(
                                            nextBooking.getId(),
                                            nextBooking.getBooker().getId()
                                    )));

                    List<CommentResponseDto> comments = commentsByItemId
                            .getOrDefault(item.getId(), Collections.emptyList())
                            .stream()
                            .map(this::toCommentResponseDto)
                            .sorted(Comparator.comparing(CommentResponseDto::getCreated).reversed())
                            .collect(Collectors.toList());
                    dto.setComments(comments);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.searchAvailableItems(text, pageRequest)
                .stream()
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

        boolean hasBooked = bookingRepository.hasUserBookedItem(authorId, itemId, LocalDateTime.now());
        if (!hasBooked) {
            throw new ValidationException("User can only comment on items they have booked");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return toCommentResponseDto(savedComment);
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