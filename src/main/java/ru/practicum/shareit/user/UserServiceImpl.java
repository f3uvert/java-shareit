package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user: name={}, email={}", userDto.getName(), userDto.getEmail());

        try {
            if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

            String email = userDto.getEmail().toLowerCase().trim();

            if (userRepository.findByEmail(email).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }

            User user = new User();
            user.setName(userDto.getName());
            user.setEmail(email);

            User savedUser = userRepository.save(user);
            log.info("User created successfully: id={}", savedUser.getId());

            return toUserDto(savedUser);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            String newEmail = userDto.getEmail().toLowerCase().trim();
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userRepository.save(existingUser);
        return toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        return toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }
}