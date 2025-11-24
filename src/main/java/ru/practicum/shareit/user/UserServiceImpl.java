package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emailToUserId = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public UserDto createUser(UserDto userDto) {
        if (emailToUserId.containsKey(userDto.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

        User user = UserMapper.toUser(userDto);
        user.setId(idCounter.getAndIncrement());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = users.get(userId);
        if (existingUser == null) {
            throw new NoSuchElementException("User not found with id: " + userId);
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            String newEmail = userDto.getEmail().toLowerCase();
            if (emailToUserId.containsKey(newEmail)) {
                throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
            }

            emailToUserId.remove(existingUser.getEmail().toLowerCase());
            emailToUserId.put(newEmail, userId);
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        users.put(userId, existingUser);
        return UserMapper.toUserDto(existingUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found with id: " + userId);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void deleteUser(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found with id: " + userId);
        }

        emailToUserId.remove(user.getEmail().toLowerCase());
        users.remove(userId);
    }
}