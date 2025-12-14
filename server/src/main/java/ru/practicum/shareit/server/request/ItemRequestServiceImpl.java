package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemForRequestDto;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + requestorId));

        ItemRequest request = new ItemRequest();
        request.setDescription(itemRequestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(request);


        return new ItemRequestDto(
                savedRequest.getId(),
                savedRequest.getDescription(),
                savedRequest.getRequestor().getId(),
                savedRequest.getCreated()
        );
    }

    @Override
    public List<ItemRequestWithItemsDto> getRequestsByRequestor(Long requestorId) {
        userRepository.findById(requestorId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + requestorId));

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
        return getRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageRequest);
        return getRequestsWithItems(requests);
    }

    @Override
    public ItemRequestWithItemsDto getRequestById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request not found with id: " + requestId));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemForRequestDto> itemDtos = items.stream()
                .map(this::toItemForRequestDto)
                .collect(Collectors.toList());

        return toItemRequestWithItemsDto(request, itemDtos);
    }

    private List<ItemRequestWithItemsDto> getRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> {
                    List<ItemForRequestDto> itemDtos = itemsByRequestId
                            .getOrDefault(request.getId(), Collections.emptyList())
                            .stream()
                            .map(this::toItemForRequestDto)
                            .collect(Collectors.toList());
                    return toItemRequestWithItemsDto(request, itemDtos);
                })
                .collect(Collectors.toList());
    }

    private ItemRequestWithItemsDto toItemRequestWithItemsDto(ItemRequest request, List<ItemForRequestDto> items) {
        return new ItemRequestWithItemsDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated(),
                items
        );
    }

    private ItemForRequestDto toItemForRequestDto(Item item) {
        return new ItemForRequestDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest().getId()
        );
    }
}