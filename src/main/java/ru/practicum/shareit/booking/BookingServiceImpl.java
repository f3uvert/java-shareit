package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final Map<Long, Booking> bookings = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Long bookerId) {
        userService.getUserById(bookerId);

        ru.practicum.shareit.item.dto.ItemDto itemDto = itemService.getItemById(bookingDto.getItemId());
        if (!Boolean.TRUE.equals(itemDto.getAvailable())) {
            throw new IllegalArgumentException("Item is not available for booking");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }

        User booker = new User();
        booker.setId(bookerId);

        Item item = new Item();
        item.setId(bookingDto.getItemId());

        Booking booking = new Booking();
        booking.setId(idCounter.getAndIncrement());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        bookings.put(booking.getId(), booking);
        return toBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new NoSuchElementException("Booking not found with id: " + bookingId);
        }

        ru.practicum.shareit.item.dto.ItemDto itemDto = itemService.getItemById(booking.getItem().getId());

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalArgumentException("Booking is already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookings.put(bookingId, booking);
        return toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new NoSuchElementException("Booking not found with id: " + bookingId);
        }

        ru.practicum.shareit.item.dto.ItemDto itemDto = itemService.getItemById(booking.getItem().getId());
        if (!booking.getBooker().getId().equals(userId)) {
            throw new SecurityException("User does not have access to this booking");
        }

        return toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Long bookerId, String state) {
        userService.getUserById(bookerId);

        return bookings.values().stream()
                .filter(booking -> booking.getBooker().getId().equals(bookerId))
                .filter(booking -> filterByState(booking, state))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId, String state) {
        userService.getUserById(ownerId);

        List<ru.practicum.shareit.item.dto.ItemDto> ownerItems = itemService.getItemsByOwner(ownerId);
        Set<Long> ownerItemIds = ownerItems.stream()
                .map(ru.practicum.shareit.item.dto.ItemDto::getId)
                .collect(Collectors.toSet());

        return bookings.values().stream()
                .filter(booking -> ownerItemIds.contains(booking.getItem().getId()))
                .filter(booking -> filterByState(booking, state))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .map(this::toBookingDto)
                .collect(Collectors.toList());
    }

    private boolean filterByState(Booking booking, String state) {
        if (state == null || state.equals("ALL")) {
            return true;
        }

        switch (state.toUpperCase()) {
            case "CURRENT":
                return booking.getStart().isBefore(LocalDateTime.now()) &&
                        booking.getEnd().isAfter(LocalDateTime.now());
            case "PAST":
                return booking.getEnd().isBefore(LocalDateTime.now());
            case "FUTURE":
                return booking.getStart().isAfter(LocalDateTime.now());
            case "WAITING":
                return booking.getStatus() == BookingStatus.WAITING;
            case "REJECTED":
                return booking.getStatus() == BookingStatus.REJECTED;
            default:
                return true;
        }
    }

    private BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus().name()
        );
    }
}