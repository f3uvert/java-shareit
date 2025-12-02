package ru.practicum.shareit.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;

import java.util.Map;

@Service
@Slf4j
public class RequestClient extends BaseClient {

    private final DtoConverter dtoConverter;

    public RequestClient(RestTemplate rest, DtoConverter dtoConverter) {
        super(rest);
        this.dtoConverter = dtoConverter;
    }

    public ResponseEntity<Object> createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        String path = "/requests";
        Map<String, Object> requestBody = dtoConverter.toServerItemRequestDto(itemRequestDto);
        log.debug("Creating request for user {}", requestorId);
        return post(path, requestorId, requestBody);
    }

    public Object getRequestsByRequestor(Long requestorId) {
        String path = "/requests";
        log.debug("Getting requests for requestor {}", requestorId);
        ResponseEntity<Object> response = get(path, requestorId);
        return response.getBody();
    }

    public Object getAllRequests(Long userId, int from, int size) {
        String path = "/requests/all?from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        log.debug("Getting all requests for user {}", userId);
        ResponseEntity<Object> response = get(path, userId, parameters);
        return response.getBody();
    }

    public Object getRequestById(Long requestId, Long userId) {
        String path = "/requests/{requestId}";
        Map<String, Object> parameters = Map.of("requestId", requestId);
        log.debug("Getting request {} for user {}", requestId, userId);
        ResponseEntity<Object> response = get(path, userId, parameters);
        return response.getBody();
    }
}