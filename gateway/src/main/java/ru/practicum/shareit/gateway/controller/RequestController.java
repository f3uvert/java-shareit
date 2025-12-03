package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final ru.practicum.shareit.gateway.client.RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ru.practicum.shareit.gateway.dto.ItemRequestDto itemRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Gateway: POST /requests | User-ID: {}", requestorId);
        return requestClient.createRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Gateway: GET /requests | User-ID: {}", requestorId);
        ResponseEntity<Object> response = requestClient.getRequestsByRequestor(requestorId);
        log.info("Gateway: Response type: {}, status: {}",
                response.getBody() != null ? response.getBody().getClass() : "null",
                response.getStatusCode());
        return response;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("Gateway: GET /requests/all | From: {}, Size: {}", from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable Long requestId,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: GET /requests/{}", requestId);
        return requestClient.getRequestById(requestId, userId);
    }
}