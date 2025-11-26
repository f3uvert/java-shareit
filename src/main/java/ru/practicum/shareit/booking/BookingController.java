package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@Valid @RequestBody BookingDto bookingDto, // ← @Valid добавлен
                                            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("POST /bookings | User-ID: {} | Item: {} | Period: {} -> {}",
                bookerId, bookingDto.getItemId(), bookingDto.getStart(), bookingDto.getEnd());

        BookingResponseDto result = bookingService.createBooking(bookingDto, bookerId);
        log.info("POST /bookings | Created booking ID: {}", result.getId());

        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("PATCH /bookings/{} | User-ID: {} | Approved: {}", bookingId, ownerId, approved);

        BookingResponseDto result = bookingService.approveBooking(bookingId, ownerId, approved);
        log.info("PATCH /bookings/{} | Status updated to: {}", bookingId, result.getStatus());

        return result;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /bookings/{} | User-ID: {}", bookingId, userId);

        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("GET /bookings | Booker-ID: {} | State: {} | From: {} | Size: {}",
                bookerId, state, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getBookingsByBooker(bookerId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        log.info("GET /bookings/owner | Owner-ID: {} | State: {} | From: {} | Size: {}",
                ownerId, state, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        return bookingService.getBookingsByOwner(ownerId, state, pageable);
    }
}