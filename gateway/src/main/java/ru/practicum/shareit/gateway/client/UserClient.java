package ru.practicum.shareit.gateway.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.dto.UserDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    public UserClient(RestTemplate rest) {
        super(rest);
    }

    public Object createUser(UserDto userDto) {
        String path = "/users";
        ResponseEntity<Object> response = post(path, userDto);
        return response.getBody();
    }

    public Object updateUser(Long userId, UserDto userDto) {
        String path = "/users/{userId}";
        Map<String, Object> parameters = Map.of("userId", userId);
        ResponseEntity<Object> response = patch(path, null, parameters, userDto);
        return response.getBody();
    }

    public Object getUserById(Long userId) {
        String path = "/users/{userId}";
        Map<String, Object> parameters = Map.of("userId", userId);
        ResponseEntity<Object> response = get(path, parameters.size());
        return response.getBody();
    }

    public Object getAllUsers() {
        String path = "/users";
        ResponseEntity<Object> response = get(path);
        return response.getBody();
    }

    public void deleteUser(Long userId) {
        String path = "/users/{userId}";
        Map<String, Object> parameters = Map.of("userId", userId);
        delete(path, parameters.size());
    }
}