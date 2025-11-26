package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingDto bookingDto, Long bookerId) {
        log.info("Creating booking for user: {}, item: {}", bookerId, bookingDto.getItemId());

        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + bookerId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Item not found with id: " + bookingDto.getItemId()));

        validateBookingCreation(bookingDto, bookerId, item);

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully: id={}", savedBooking.getId());

        return toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        log.info("Approving booking: bookingId={}, ownerId={}, approved={}", bookingId, ownerId, approved);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + ownerId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.warn("Access denied: user {} is not owner of item {}", ownerId, booking.getItem().getId());
            throw new SecurityException("Only item owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking is already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);

        log.info("Booking approved successfully: id={}, newStatus={}", updatedBooking.getId(), updatedBooking.getStatus());
        return toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        log.info("Getting booking: bookingId={}, userId={}", bookingId, userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found with id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NoSuchElementException("User does not have access to this booking");
        }

        return toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByBooker(Long bookerId, String state, Pageable pageable) {
        log.info("Getting bookings for booker: bookerId={}, state={}", bookerId, state);

        userRepository.findById(bookerId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + bookerId));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByBookerId(bookerId, now, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(this::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state, Pageable pageable) {
        log.info("Getting bookings for owner: ownerId={}, state={}", ownerId, state);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + ownerId));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwnerId(ownerId, now, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(this::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private void validateBookingCreation(BookingDto bookingDto, Long bookerId, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new NoSuchElementException("Owner cannot book own item");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new ValidationException("End date must be after start date");
        }

        if (bookingRepository.existsApprovedBookingForItemInPeriod(
                item.getId(), bookingDto.getStart(), bookingDto.getEnd())) {
            throw new ValidationException("Item is already booked for this period");
        }
    }

    private BookingResponseDto toBookingResponseDto(Booking booking) {
        BookingResponseDto.BookingItemDto itemDto = new BookingResponseDto.BookingItemDto(
                booking.getItem().getId(),
                booking.getItem().getName()
        );

        BookingResponseDto.BookingUserDto userDto = new BookingResponseDto.BookingUserDto(
                booking.getBooker().getId(),
                booking.getBooker().getName()
        );

        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                userDto,
                booking.getStatus()
        );
    }
}