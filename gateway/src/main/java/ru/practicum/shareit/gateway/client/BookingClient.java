package ru.practicum.shareit.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.BookingDto;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {

    private final DtoConverter dtoConverter;

    public BookingClient(RestTemplate rest, DtoConverter dtoConverter) {
        super(rest);
        this.dtoConverter = dtoConverter;
    }

    public Object createBooking(BookingDto bookingDto, Long bookerId) {
        String path = "/bookings";
        Map<String, Object> requestBody = dtoConverter.toServerBookingDto(bookingDto);
        log.debug("Creating booking: {}", requestBody);
        return post(path, bookerId, requestBody).getBody();
    }

    public Object approveBooking(Long bookingId, Long ownerId, boolean approved) {
        String path = "/bookings/{bookingId}?approved={approved}";
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );
        log.debug("Approving booking {}: approved={}", bookingId, approved);
        return patch(path, ownerId, parameters, null).getBody();
    }

    public Object getBookingById(Long bookingId, Long userId) {
        String path = "/bookings/{bookingId}";
        Map<String, Object> parameters = Map.of("bookingId", bookingId);
        log.debug("Getting booking {} for user {}", bookingId, userId);
        return get(path, userId, parameters).getBody();
    }

    public Object getBookingsByBooker(Long bookerId, String state, int from, int size) {
        String path = "/bookings?state={state}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        log.debug("Getting bookings for booker {} with state {}", bookerId, state);
        return get(path, bookerId, parameters).getBody();
    }

    public Object getBookingsByOwner(Long ownerId, String state, int from, int size) {
        String path = "/bookings/owner?state={state}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        log.debug("Getting bookings for owner {} with state {}", ownerId, state);
        return get(path, ownerId, parameters).getBody();
    }
}