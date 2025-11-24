package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final Map<Long, ItemRequest> requests = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);
    private final UserService userService;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        // Проверяем, что пользователь существует
        userService.getUserById(requestorId);

        User requestor = new User();
        requestor.setId(requestorId);

        ItemRequest request = new ItemRequest();
        request.setId(idCounter.getAndIncrement());
        request.setDescription(itemRequestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        requests.put(request.getId(), request);
        return toItemRequestDto(request);
    }

    @Override
    public List<ItemRequestDto> getRequestsByRequestor(Long requestorId) {
        userService.getUserById(requestorId); // Проверяем существование пользователя

        return requests.values().stream()
                .filter(request -> request.getRequestor().getId().equals(requestorId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        userService.getUserById(userId); // Проверяем существование пользователя

        return requests.values().stream()
                .filter(request -> !request.getRequestor().getId().equals(userId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .skip(from)
                .limit(size)
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        userService.getUserById(userId); // Проверяем существование пользователя

        ItemRequest request = requests.get(requestId);
        if (request == null) {
            throw new NoSuchElementException("Request not found with id: " + requestId);
        }
        return toItemRequestDto(request);
    }

    private ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated()
        );
    }
}