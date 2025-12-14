package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final ru.practicum.shareit.gateway.client.UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody ru.practicum.shareit.gateway.dto.UserDto userDto) {
        log.info("Gateway: POST /users | Creating user: name='{}', email='{}'",
                userDto.getName(), userDto.getEmail());
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @Valid @RequestBody ru.practicum.shareit.gateway.dto.UserUpdateDto userUpdateDto) {
        log.info("Gateway: PATCH /users/{} | Updating user", userId);

        ru.practicum.shareit.gateway.dto.UserDto userDto = new ru.practicum.shareit.gateway.dto.UserDto(
                userUpdateDto.getId(),
                userUpdateDto.getName(),
                userUpdateDto.getEmail()
        );
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Gateway: GET /users/{} | Getting user by ID", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Gateway: GET /users | Getting all users");
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Gateway: DELETE /users/{} | Deleting user", userId);
        return userClient.deleteUser(userId);
    }
}