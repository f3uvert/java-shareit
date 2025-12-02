package ru.practicum.shareit.gateway.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.converter.DtoConverter;
import ru.practicum.shareit.gateway.dto.UserDto;

import java.util.Map;

@Service
@Slf4j
public class UserClient extends BaseClient {

    private final DtoConverter dtoConverter;

    public UserClient(RestTemplate rest, DtoConverter dtoConverter) {
        super(rest);
        this.dtoConverter = dtoConverter;
    }

    public Object createUser(UserDto userDto) {
        String path = "/users";
        Map<String, Object> requestBody = dtoConverter.toServerUserDto(userDto);
        log.debug("Creating user: {}", userDto.getEmail());
        return post(path, requestBody).getBody();
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        String path = "/users/{userId}";
        Map<String, Object> parameters = Map.of("userId", userId);
        Map<String, Object> requestBody = dtoConverter.toServerUserDto(userDto);
        log.debug("Updating user {}: {}", userId, userDto.getEmail());
        return patch(path, null, parameters, requestBody);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        String path = "/users/{userId}";
        Map<String, Object> parameters = Map.of("userId", userId);
        log.debug("Getting user by id: {}", userId);
        return get(path, parameters);
    }

    public ResponseEntity<Object> getAllUsers() {
        log.debug("Getting all users");
        return get("/users");
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        String path = "/users/{userId}";
        Map<String, Object> parameters = Map.of("userId", userId);
        log.debug("Deleting user: {}", userId);
        return delete(path, parameters.size());
    }
}