package ru.practicum.shareit.gateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;
import ru.practicum.shareit.gateway.dto.ItemRequestWithItemsDto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestClient {
    private final WebClient webClient;
    private final DtoConverter converter;

    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        log.debug("Calling server to create request for user {}", requestorId);

        Map<String, Object> requestBody = converter.toServerItemRequestDto(itemRequestDto);

        return webClient.post()
                .uri("/requests")
                .header("X-Sharer-User-Id", String.valueOf(requestorId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(Map.class)
                .map(converter::toGatewayItemRequestDto)
                .block();
    }

    public List<ItemRequestWithItemsDto> getRequestsByRequestor(Long requestorId) {
        log.debug("Calling server to get requests for user {}", requestorId);

        return webClient.get()
                .uri("/requests")
                .header("X-Sharer-User-Id", String.valueOf(requestorId))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(list -> list.stream()
                        .map(converter::toGatewayItemRequestWithItemsDto)
                        .collect(Collectors.toList()))
                .block();
    }

    public List<ItemRequestWithItemsDto> getAllRequests(Long userId, int from, int size) {
        log.debug("Calling server to get all requests (from={}, size={})", from, size);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/requests/all")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .map(list -> list.stream()
                        .map(converter::toGatewayItemRequestWithItemsDto)
                        .collect(Collectors.toList()))
                .block();
    }

    public ItemRequestWithItemsDto getRequestById(Long requestId, Long userId) {
        log.debug("Calling server to get request {}", requestId);

        return webClient.get()
                .uri("/requests/{requestId}", requestId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(Map.class)
                .map(converter::toGatewayItemRequestWithItemsDto)
                .block();
    }
}