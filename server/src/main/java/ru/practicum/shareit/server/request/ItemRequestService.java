package ru.practicum.shareit.server.request;



import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long requestorId);

    List<ItemRequestWithItemsDto> getRequestsByRequestor(Long requestorId);

    List<ItemRequestWithItemsDto> getAllRequests(Long userId, int from, int size);

    ItemRequestWithItemsDto getRequestById(Long requestId, Long userId);
}