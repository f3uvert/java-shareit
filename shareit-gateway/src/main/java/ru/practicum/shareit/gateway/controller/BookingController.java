package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.BookingDto;
import ru.practicum.shareit.gateway.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public BookingResponseDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        log.info("Gateway: POST /bookings | User-ID: {}", bookerId);
        return bookingClient.createBooking(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@PathVariable Long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Gateway: PATCH /bookings/{} | Approved: {}", bookingId, approved);
        return bookingClient.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: GET /bookings/{}", bookingId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Gateway: GET /bookings | State: {}", state);
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingClient.getBookingsByBooker(bookerId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        log.info("Gateway: GET /bookings/owner | State: {}", state);
        Pageable pageable = PageRequest.of(from / size, size);
        return bookingClient.getBookingsByOwner(ownerId, state, pageable);
    }
}