package ru.practicum.shareit.gateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClient {
    private final WebClient webClient;
    private final DtoConverter converter;

    public UserClient(@Value("${shareit.server.url}") String serverUrl, DtoConverter converter) {
        this.webClient = WebClient.builder()
                .baseUrl(serverUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.converter = converter;
    }

    public UserDto createUser(UserDto userDto) {
        log.debug("Calling server to create user {}", userDto.getEmail());

        return converter.toGatewayUserDto(
                webClient.post()
                        .uri("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(converter.toServerUserDto(userDto))
                        .retrieve()
                        .bodyToMono(ru.practicum.shareit.server.user.dto.UserDto.class)
                        .block()
        );
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        log.debug("Calling server to update user {}", userId);

        return converter.toGatewayUserDto(
                webClient.patch()
                        .uri("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(converter.toServerUserDto(userDto))
                        .retrieve()
                        .bodyToMono(ru.practicum.shareit.server.user.dto.UserDto.class)
                        .block()
        );
    }

    public UserDto getUserById(Long userId) {
        log.debug("Calling server to get user {}", userId);

        return converter.toGatewayUserDto(
                webClient.get()
                        .uri("/users/{userId}", userId)
                        .retrieve()
                        .bodyToMono(ru.practicum.shareit.server.user.dto.UserDto.class)
                        .block()
        );
    }

    public List<UserDto> getAllUsers() {
        log.debug("Calling server to get all users");

        List<ru.practicum.shareit.server.user.dto.UserDto> serverUsers = webClient.get()
                .uri("/users")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ru.practicum.shareit.server.user.dto.UserDto>>() {})
                .block();

        return serverUsers.stream()
                .map(converter::toGatewayUserDto)
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteUser(Long userId) {
        log.debug("Calling server to delete user {}", userId);

        webClient.delete()
                .uri("/users/{userId}", userId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}