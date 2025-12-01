package ru.practicum.shareit.gateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;
import ru.practicum.shareit.gateway.dto.ItemRequestWithItemsDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestClient {
    private final WebClient webClient;
    private final DtoConverter converter;

    public RequestClient(@Value("${shareit.server.url}") String serverUrl, DtoConverter converter) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.converter = converter;
    }

    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        log.debug("Calling server to create request for user {}", requestorId);

        return converter.toGatewayRequestDto(
                webClient.post()
                        .uri("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(requestorId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(converter.toServerRequestDto(itemRequestDto))
                        .retrieve()
                        .bodyToMono(ru.practicum.shareit.server.request.dto.ItemRequestDto.class)
                        .block()
        );
    }

    public List<ItemRequestWithItemsDto> getRequestsByRequestor(Long requestorId) {
        log.debug("Calling server to get requests for user {}", requestorId);

        return webClient.get()
                .uri("/requests")
                .header("X-Sharer-User-Id", String.valueOf(requestorId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemRequestWithItemsDto>>() {})
                .block();
    }

    public List<ItemRequestWithItemsDto> getAllRequests(Long userId, int from, int size) {
        log.debug("Calling server to get all requests");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/requests/all")
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemRequestWithItemsDto>>() {})
                .block();
    }

    public ItemRequestWithItemsDto getRequestById(Long requestId, Long userId) {
        log.debug("Calling server to get request {}", requestId);

        return webClient.get()
                .uri("/requests/{requestId}", requestId)
                .header("X-Sharer-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(ItemRequestWithItemsDto.class)
                .block();
    }
}