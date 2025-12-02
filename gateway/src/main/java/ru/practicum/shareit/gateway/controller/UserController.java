package ru.practicum.shareit.gateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.UserClient;
import ru.practicum.shareit.gateway.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Gateway: POST /users | Creating user: name='{}', email='{}'",
                userDto.getName(), userDto.getEmail());

        UserDto createdUser = (UserDto) userClient.createUser(userDto);
        log.info("Gateway: User created successfully | ID: {}, Email: {}",
                createdUser.getId(), createdUser.getEmail());

        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Valid @RequestBody UserDto userDto) {
        log.info("Gateway: PATCH /users/{} | Updating user", userId);
        log.debug("Gateway: Update data - name: {}, email: {}",
                userDto.getName(), userDto.getEmail());

        UserDto updatedUser = (UserDto) userClient.updateUser(userId, userDto);
        log.info("Gateway: User {} updated successfully", userId);

        return updatedUser;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Gateway: GET /users/{} | Getting user by ID", userId);

        UserDto user = (UserDto) userClient.getUserById(userId);
        log.info("Gateway: User {} retrieved successfully | Name: {}",
                userId, user.getName());

        return user;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Gateway: GET /users | Getting all users");

        List<UserDto> users = (List<UserDto>) userClient.getAllUsers();
        log.info("Gateway: Retrieved {} users", users.size());

        return users;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Gateway: DELETE /users/{} | Deleting user", userId);

        userClient.deleteUser(userId);
        log.info("Gateway: User {} deleted successfully", userId);
    }
}