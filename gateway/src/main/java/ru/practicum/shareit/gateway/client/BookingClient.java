package ru.practicum.shareit.gateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.BookingDto;
import ru.practicum.shareit.gateway.dto.BookingResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingClient {
    private final WebClient webClient;
    private final DtoConverter converter;

    @Value("${shareit.server.url}")
    private String serverUrl;

    public BookingResponseDto createBooking(BookingDto bookingDto, Long bookerId) {
        log.debug("Calling server to create booking for user {}", bookerId);

        return webClient.post()
                .uri(serverUrl + "/bookings")  // Добавляем базовый URL
                .header("X-Sharer-User-Id", String.valueOf(bookerId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(converter.toServerBookingDto(bookingDto))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(BookingResponseDto.class)
                .block();
    }

    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, boolean approved) {
        log.debug("Calling server to approve booking {}", bookingId);

        return webClient.patch()
                .uri(serverUrl + "/bookings/{bookingId}?approved={approved}", bookingId, approved)
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .retrieve()
                .bodyToMono(BookingResponseDto.class)
                .block();
    }

    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        log.debug("Calling server to get booking {}", bookingId);

        return webClient.get()
                .uri(serverUrl + "/bookings/{bookingId}", bookingId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(BookingResponseDto.class)
                .block();
    }

    public List<BookingResponseDto> getBookingsByBooker(Long bookerId, String state, Pageable pageable) {
        log.debug("Calling server to get bookings for booker {}", bookerId);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(serverUrl + "/bookings")
                        .queryParam("state", state)
                        .queryParam("from", pageable.getPageNumber() * pageable.getPageSize())
                        .queryParam("size", pageable.getPageSize())
                        .build())
                .header("X-Sharer-User-Id", String.valueOf(bookerId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BookingResponseDto>>() {})
                .block();
    }

    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state, Pageable pageable) {
        log.debug("Calling server to get bookings for owner {}", ownerId);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(serverUrl + "/bookings/owner")
                        .queryParam("state", state)
                        .queryParam("from", pageable.getPageNumber() * pageable.getPageSize())
                        .queryParam("size", pageable.getPageSize())
                        .build())
                .header("X-Sharer-User-Id", String.valueOf(ownerId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<BookingResponseDto>>() {})
                .block();
    }
}