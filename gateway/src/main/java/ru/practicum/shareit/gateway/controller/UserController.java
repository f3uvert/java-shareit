package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.UserDto;
import ru.practicum.shareit.gateway.dto.UserUpdateDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Gateway: POST /users | Creating user: name='{}', email='{}'",
                userDto.getName(), userDto.getEmail());

        ResponseEntity<Object> response = userClient.createUser(userDto);
        log.info("Gateway: Response status: {}", response.getStatusCode());

        return response;
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Gateway: PATCH /users/{} | Updating user", userId);

        UserDto userDto = new UserDto(userUpdateDto.getId(), userUpdateDto.getName(), userUpdateDto.getEmail());
        ResponseEntity<Object> response = userClient.updateUser(userId, userDto);
        log.info("Gateway: Response status: {}", response.getStatusCode());

        return response;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Gateway: GET /users/{} | Getting user by ID", userId);

        ResponseEntity<Object> response = userClient.getUserById(userId);
        log.info("Gateway: Response status: {}", response.getStatusCode());

        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Gateway: GET /users | Getting all users");

        ResponseEntity<Object> response = userClient.getAllUsers();
        log.info("Gateway: Response status: {}", response.getStatusCode());

        return response;
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Gateway: DELETE /users/{} | Deleting user", userId);

        ResponseEntity<Object> response = userClient.deleteUser(userId);
        log.info("Gateway: Response status: {}", response.getStatusCode());

        return response;
    }
}