package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        userService.getUserById(requestorId);

        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());

        ru.practicum.shareit.user.User requestor = new ru.practicum.shareit.user.User();
        requestor.setId(requestorId);
        request.setRequestor(requestor);

        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(request);
        return toItemRequestDto(savedRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsByRequestor(Long requestorId) {
        userService.getUserById(requestorId);

        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId).stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        userService.getUserById(userId);

        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId).stream()
                .skip(from)
                .limit(size)
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        userService.getUserById(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found with id: " + requestId));
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