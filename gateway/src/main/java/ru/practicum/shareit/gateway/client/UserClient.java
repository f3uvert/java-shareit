package ru.practicum.shareit.gateway.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.gateway.dto.UserDto;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserClient {
    private final WebClient webClient;

    public UserDto createUser(UserDto userDto) {
        log.debug("Calling server to create user {}", userDto.getEmail());

        Map<String, Object> requestBody = Map.of(
                "name", userDto.getName(),
                "email", userDto.getEmail()
        );

        return webClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(UserDto.class)
                .block();
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        log.debug("Calling server to update user {}", userId);

        Map<String, Object> requestBody = Map.of(
                "name", userDto.getName(),
                "email", userDto.getEmail()
        );

        return webClient.patch()
                .uri("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(UserDto.class)
                .block();
    }

    public UserDto getUserById(Long userId) {
        log.debug("Calling server to get user {}", userId);

        return webClient.get()
                .uri("/users/{userId}", userId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(UserDto.class)
                .block();
    }

    public List<UserDto> getAllUsers() {
        log.debug("Calling server to get all users");

        return webClient.get()
                .uri("/users")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {})
                .block();
    }

    public void deleteUser(Long userId) {
        log.debug("Calling server to delete user {}", userId);

        webClient.delete()
                .uri("/users/{userId}", userId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Server error: " + error))))
                .toBodilessEntity()
                .block();
    }
}