package ru.practicum.shareit.gateway.converter;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.gateway.dto.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DtoConverter {

    public Map<String, Object> toServerUserDto(UserDto gatewayDto) {
        Map<String, Object> map = new HashMap<>();
        if (gatewayDto.getId() != null) {
            map.put("id", gatewayDto.getId());
        }
        if (gatewayDto.getName() != null) {
            map.put("name", gatewayDto.getName());
        }
        if (gatewayDto.getEmail() != null) {
            map.put("email", gatewayDto.getEmail());
        }
        return map;
    }

    public Map<String, Object> toServerItemDto(ItemDto gatewayDto) {
        Map<String, Object> map = new HashMap<>();
        if (gatewayDto.getId() != null) {
            map.put("id", gatewayDto.getId());
        }
        if (gatewayDto.getName() != null) {
            map.put("name", gatewayDto.getName());
        }
        if (gatewayDto.getDescription() != null) {
            map.put("description", gatewayDto.getDescription());
        }
        if (gatewayDto.getAvailable() != null) {
            map.put("available", gatewayDto.getAvailable());
        }
        if (gatewayDto.getRequestId() != null) {
            map.put("requestId", gatewayDto.getRequestId());
        }
        return map;
    }

    public Map<String, Object> toServerItemUpdateDto(ItemUpdateDto gatewayDto) {
        Map<String, Object> map = new HashMap<>();
        if (gatewayDto.getId() != null) {
            map.put("id", gatewayDto.getId());
        }
        if (gatewayDto.getName() != null) {
            map.put("name", gatewayDto.getName());
        }
        if (gatewayDto.getDescription() != null) {
            map.put("description", gatewayDto.getDescription());
        }
        if (gatewayDto.getAvailable() != null) {
            map.put("available", gatewayDto.getAvailable());
        }
        if (gatewayDto.getRequestId() != null) {
            map.put("requestId", gatewayDto.getRequestId());
        }
        return map;
    }

    public Map<String, Object> toServerBookingDto(BookingDto gatewayDto) {
        Map<String, Object> map = new HashMap<>();
        if (gatewayDto.getId() != null) {
            map.put("id", gatewayDto.getId());
        }
        if (gatewayDto.getStart() != null) {
            map.put("start", gatewayDto.getStart());
        }
        if (gatewayDto.getEnd() != null) {
            map.put("end", gatewayDto.getEnd());
        }
        if (gatewayDto.getItemId() != null) {
            map.put("itemId", gatewayDto.getItemId());
        }
        if (gatewayDto.getBookerId() != null) {
            map.put("bookerId", gatewayDto.getBookerId());
        }
        if (gatewayDto.getStatus() != null) {
            map.put("status", gatewayDto.getStatus());
        }
        return map;
    }

    public BookingDto toGatewayBookingDto(Map<String, Object> serverResponse) {
        BookingDto dto = new BookingDto();

        if (serverResponse.get("id") != null) {
            dto.setId(((Number) serverResponse.get("id")).longValue());
        }

        if (serverResponse.get("start") != null) {
            dto.setStart(LocalDateTime.parse(serverResponse.get("start").toString()));
        }

        if (serverResponse.get("end") != null) {
            dto.setEnd(LocalDateTime.parse(serverResponse.get("end").toString()));
        }

        if (serverResponse.get("itemId") != null) {
            dto.setItemId(((Number) serverResponse.get("itemId")).longValue());
        }

        if (serverResponse.get("bookerId") != null) {
            dto.setBookerId(((Number) serverResponse.get("bookerId")).longValue());
        }

        if (serverResponse.get("status") != null) {
            dto.setStatus(serverResponse.get("status").toString());
        }

        return dto;
    }

    public Map<String, Object> toServerCommentDto(CommentDto gatewayDto) {
        Map<String, Object> map = new HashMap<>();
        if (gatewayDto.getText() != null && !gatewayDto.getText().isEmpty()) {
            map.put("text", gatewayDto.getText());
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public CommentResponseDto toGatewayCommentResponseDto(Map<String, Object> serverResponse) {
        CommentResponseDto dto = new CommentResponseDto();

        if (serverResponse.get("id") != null) {
            dto.setId(((Number) serverResponse.get("id")).longValue());
        }

        if (serverResponse.get("text") != null) {
            dto.setText(serverResponse.get("text").toString());
        }

        if (serverResponse.get("authorName") != null) {
            dto.setAuthorName(serverResponse.get("authorName").toString());
        }

        if (serverResponse.get("created") != null) {
            dto.setCreated(LocalDateTime.parse(serverResponse.get("created").toString()));
        }

        return dto;
    }

    public Map<String, Object> toServerItemRequestDto(ItemRequestDto gatewayDto) {
        Map<String, Object> map = new HashMap<>();
        if (gatewayDto.getDescription() != null && !gatewayDto.getDescription().isEmpty()) {
            map.put("description", gatewayDto.getDescription());
        }
        return map;
    }

    public ItemRequestDto toGatewayItemRequestDto(Map<String, Object> serverResponse) {
        ItemRequestDto dto = new ItemRequestDto();

        if (serverResponse.get("id") != null) {
            dto.setId(((Number) serverResponse.get("id")).longValue());
        }

        if (serverResponse.get("description") != null) {
            dto.setDescription(serverResponse.get("description").toString());
        }

        if (serverResponse.get("requestorId") != null) {
            dto.setRequestorId(((Number) serverResponse.get("requestorId")).longValue());
        }

        if (serverResponse.get("created") != null) {
            dto.setCreated(LocalDateTime.parse(serverResponse.get("created").toString()));
        }

        return dto;
    }

    @SuppressWarnings("unchecked")
    public ItemRequestWithItemsDto toGatewayItemRequestWithItemsDto(Map<String, Object> serverResponse) {
        ItemRequestWithItemsDto dto = new ItemRequestWithItemsDto();

        if (serverResponse.get("id") != null) {
            dto.setId(((Number) serverResponse.get("id")).longValue());
        }

        if (serverResponse.get("description") != null) {
            dto.setDescription(serverResponse.get("description").toString());
        }

        if (serverResponse.get("requestorId") != null) {
            dto.setRequestorId(((Number) serverResponse.get("requestorId")).longValue());
        }

        if (serverResponse.get("created") != null) {
            dto.setCreated(LocalDateTime.parse(serverResponse.get("created").toString()));
        }

        if (serverResponse.get("items") != null && serverResponse.get("items") instanceof List) {
            List<Map<String, Object>> itemsList = (List<Map<String, Object>>) serverResponse.get("items");
            List<ItemForRequestDto> items = itemsList.stream()
                    .map(this::toGatewayItemForRequestDto)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }

        return dto;
    }

    @SuppressWarnings("unchecked")
    public ItemForRequestDto toGatewayItemForRequestDto(Map<String, Object> serverResponse) {
        ItemForRequestDto dto = new ItemForRequestDto();

        if (serverResponse.get("id") != null) {
            dto.setId(((Number) serverResponse.get("id")).longValue());
        }

        if (serverResponse.get("name") != null) {
            dto.setName(serverResponse.get("name").toString());
        }

        if (serverResponse.get("description") != null) {
            dto.setDescription(serverResponse.get("description").toString());
        }

        if (serverResponse.get("available") != null) {
            dto.setAvailable(Boolean.parseBoolean(serverResponse.get("available").toString()));
        }

        if (serverResponse.get("ownerId") != null) {
            dto.setOwnerId(((Number) serverResponse.get("ownerId")).longValue());
        }

        if (serverResponse.get("requestId") != null) {
            dto.setRequestId(((Number) serverResponse.get("requestId")).longValue());
        }

        return dto;
    }

    @SuppressWarnings("unchecked")
    public BookingResponseDto toGatewayBookingResponseDto(Map<String, Object> serverResponse) {
        BookingResponseDto dto = new BookingResponseDto();

        if (serverResponse.get("id") != null) {
            dto.setId(((Number) serverResponse.get("id")).longValue());
        }

        if (serverResponse.get("start") != null) {
            dto.setStart(LocalDateTime.parse(serverResponse.get("start").toString()));
        }

        if (serverResponse.get("end") != null) {
            dto.setEnd(LocalDateTime.parse(serverResponse.get("end").toString()));
        }

        if (serverResponse.get("item") != null) {
            Map<String, Object> itemMap = (Map<String, Object>) serverResponse.get("item");
            BookingResponseDto.BookingItemDto itemDto = new BookingResponseDto.BookingItemDto();

            if (itemMap.get("id") != null) {
                itemDto.setId(((Number) itemMap.get("id")).longValue());
            }

            if (itemMap.get("name") != null) {
                itemDto.setName(itemMap.get("name").toString());
            }

            dto.setItem(itemDto);
        }

        if (serverResponse.get("booker") != null) {
            Map<String, Object> bookerMap = (Map<String, Object>) serverResponse.get("booker");
            BookingResponseDto.BookingUserDto userDto = new BookingResponseDto.BookingUserDto();

            if (bookerMap.get("id") != null) {
                userDto.setId(((Number) bookerMap.get("id")).longValue());
            }

            if (bookerMap.get("name") != null) {
                userDto.setName(bookerMap.get("name").toString());
            }

            dto.setBooker(userDto);
        }


        if (serverResponse.get("status") != null) {
            String status = serverResponse.get("status").toString();
            try {
                dto.setStatus(BookingStatus.valueOf(status));
            } catch (IllegalArgumentException e) {

            }
        }

        return dto;
    }

    @SuppressWarnings("unchecked")
    public ItemWithBookingsDto toGatewayItemWithBookingsDto(Map<String, Object> serverResponse) {
        ItemWithBookingsDto dto = new ItemWithBookingsDto();

        if (serverResponse.get("id") != null) {
            dto.setId(((Number) serverResponse.get("id")).longValue());
        }

        if (serverResponse.get("name") != null) {
            dto.setName(serverResponse.get("name").toString());
        }

        if (serverResponse.get("description") != null) {
            dto.setDescription(serverResponse.get("description").toString());
        }

        if (serverResponse.get("available") != null) {
            dto.setAvailable(Boolean.parseBoolean(serverResponse.get("available").toString()));
        }

        if (serverResponse.get("lastBooking") != null) {
            Map<String, Object> bookingMap = (Map<String, Object>) serverResponse.get("lastBooking");
            ItemWithBookingsDto.BookingInfoDto bookingDto = new ItemWithBookingsDto.BookingInfoDto();

            if (bookingMap.get("id") != null) {
                bookingDto.setId(((Number) bookingMap.get("id")).longValue());
            }

            if (bookingMap.get("bookerId") != null) {
                bookingDto.setBookerId(((Number) bookingMap.get("bookerId")).longValue());
            }

            dto.setLastBooking(bookingDto);
        }

        if (serverResponse.get("nextBooking") != null) {
            Map<String, Object> bookingMap = (Map<String, Object>) serverResponse.get("nextBooking");
            ItemWithBookingsDto.BookingInfoDto bookingDto = new ItemWithBookingsDto.BookingInfoDto();

            if (bookingMap.get("id") != null) {
                bookingDto.setId(((Number) bookingMap.get("id")).longValue());
            }

            if (bookingMap.get("bookerId") != null) {
                bookingDto.setBookerId(((Number) bookingMap.get("bookerId")).longValue());
            }

            dto.setNextBooking(bookingDto);
        }

        if (serverResponse.get("comments") != null && serverResponse.get("comments") instanceof List) {
            List<Map<String, Object>> commentsList = (List<Map<String, Object>>) serverResponse.get("comments");
            List<CommentResponseDto> comments = commentsList.stream()
                    .map(this::toGatewayCommentResponseDto)
                    .collect(Collectors.toList());
            dto.setComments(comments);
        }

        if (serverResponse.get("requestId") != null) {
            dto.setRequestId(((Number) serverResponse.get("requestId")).longValue());
        }

        return dto;
    }

}