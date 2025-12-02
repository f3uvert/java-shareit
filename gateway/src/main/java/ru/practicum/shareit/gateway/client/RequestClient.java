package ru.practicum.shareit.gateway.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {

    public RequestClient(RestTemplate rest) {
        super(rest);
    }

    public Object createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        String path = "/requests";
        ResponseEntity<Object> response = post(path, requestorId, itemRequestDto);
        return response.getBody();
    }

    public Object getRequestsByRequestor(Long requestorId) {
        String path = "/requests";
        ResponseEntity<Object> response = get(path, requestorId);
        return response.getBody();
    }

    public Object getAllRequests(Long userId, int from, int size) {
        String path = "/requests/all?from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        ResponseEntity<Object> response = get(path, userId, parameters);
        return response.getBody();
    }

    public Object getRequestById(Long requestId, Long userId) {
        String path = "/requests/{requestId}";
        Map<String, Object> parameters = Map.of("requestId", requestId);
        ResponseEntity<Object> response = get(path, userId, parameters);
        return response.getBody();
    }
}