package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.RequestClient;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;
import ru.practicum.shareit.gateway.dto.ItemRequestWithItemsDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Gateway: POST /requests | User-ID: {}", requestorId);
        return (ItemRequestDto) requestClient.createRequest(itemRequestDto, requestorId);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getRequestsByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Gateway: GET /requests | User-ID: {}", requestorId);
        return (List<ItemRequestWithItemsDto>) requestClient.getRequestsByRequestor(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "0") int from,
                                                        @RequestParam(defaultValue = "10") int size) {
        log.info("Gateway: GET /requests/all | From: {}, Size: {}", from, size);
        return (List<ItemRequestWithItemsDto>) requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestById(@PathVariable Long requestId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: GET /requests/{}", requestId);
        return (ItemRequestWithItemsDto) requestClient.getRequestById(requestId, userId);
    }
}