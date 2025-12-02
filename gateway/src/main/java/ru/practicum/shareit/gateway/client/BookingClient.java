package ru.practicum.shareit.gateway.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.BookingDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    public BookingClient(RestTemplate rest) {
        super(rest);
    }

    public Object createBooking(BookingDto bookingDto, Long bookerId) {
        String path = "/bookings";
        return post(path, bookerId, bookingDto).getBody();
    }

    public Object approveBooking(Long bookingId, Long ownerId, boolean approved) {
        String path = "/bookings/{bookingId}";
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );
        return patch(path, ownerId, parameters, null).getBody();
    }

    public Object getBookingById(Long bookingId, Long userId) {
        String path = "/bookings/{bookingId}";
        Map<String, Object> parameters = Map.of("bookingId", bookingId);
        return get(path, userId, parameters).getBody();
    }

    public Object getBookingsByBooker(Long bookerId, String state, int from, int size) {
        String path = "/bookings?state={state}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get(path, bookerId, parameters).getBody();
    }

    public Object getBookingsByOwner(Long ownerId, String state, int from, int size) {
        String path = "/bookings/owner?state={state}&from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get(path, ownerId, parameters).getBody();
    }
}