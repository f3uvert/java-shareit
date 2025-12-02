package ru.practicum.shareit.server.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static ru.practicum.shareit.server.user.UserMapper.toUserDto;

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

            String email = userDto.getEmail().trim();

            if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email already exists: " + email);
            }

            User user = new User();
            user.setName(userDto.getName());
            user.setEmail(email);

            User savedUser = userRepository.save(user);
            log.info("User created successfully: id={}", savedUser.getId());

            return toUserDto(savedUser);
        } catch (ResponseStatusException e) {
            throw e; // Перебрасываем ResponseStatusException
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            String newEmail = userDto.getEmail().trim();

            if (!newEmail.equalsIgnoreCase(existingUser.getEmail())) {
                userRepository.findByEmailIgnoreCase(newEmail)
                        .ifPresent(user -> {
                            throw new ResponseStatusException(HttpStatus.CONFLICT,
                                    "Email already exists: " + newEmail);
                        });
                existingUser.setEmail(newEmail);
            }
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        try {
            User updatedUser = userRepository.save(existingUser);
            return toUserDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Email already exists");
        }
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