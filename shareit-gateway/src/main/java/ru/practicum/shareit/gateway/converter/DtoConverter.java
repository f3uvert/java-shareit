package ru.practicum.shareit.gateway.converter;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.gateway.dto.*;

@Component
public class DtoConverter {

    // User конвертеры
    public ru.practicum.shareit.server.user.dto.UserDto toServerUserDto(UserDto gatewayDto) {
        if (gatewayDto == null) {
            return null;
        }
        return new ru.practicum.shareit.server.user.dto.UserDto(
                gatewayDto.getId(),
                gatewayDto.getName(),
                gatewayDto.getEmail()
        );
    }

    public UserDto toGatewayUserDto(ru.practicum.shareit.server.user.dto.UserDto serverDto) {
        if (serverDto == null) {
            return null;
        }
        return new UserDto(
                serverDto.getId(),
                serverDto.getName(),
                serverDto.getEmail()
        );
    }

    // Item конвертеры
    public ru.practicum.shareit.server.item.dto.ItemDto toServerItemDto(ItemDto gatewayDto) {
        if (gatewayDto == null) {
            return null;
        }
        return new ru.practicum.shareit.server.item.dto.ItemDto(
                gatewayDto.getId(),
                gatewayDto.getName(),
                gatewayDto.getDescription(),
                gatewayDto.getAvailable(),
                gatewayDto.getRequestId()
        );
    }

    public ItemDto toGatewayItemDto(ru.practicum.shareit.server.item.dto.ItemDto serverDto) {
        if (serverDto == null) {
            return null;
        }
        return new ItemDto(
                serverDto.getId(),
                serverDto.getName(),
                serverDto.getDescription(),
                serverDto.getAvailable(),
                serverDto.getRequestId()
        );
    }

    public ru.practicum.shareit.server.item.dto.ItemUpdateDto toServerItemUpdateDto(ItemUpdateDto gatewayDto) {
        if (gatewayDto == null) {
            return null;
        }
        return new ru.practicum.shareit.server.item.dto.ItemUpdateDto(
                gatewayDto.getId(),
                gatewayDto.getName(),
                gatewayDto.getDescription(),
                gatewayDto.getAvailable(),
                gatewayDto.getRequestId()
        );
    }

    // Booking конвертеры
    public ru.practicum.shareit.server.booking.dto.BookingDto toServerBookingDto(BookingDto gatewayDto) {
        if (gatewayDto == null) {
            return null;
        }
        return new ru.practicum.shareit.server.booking.dto.BookingDto(
                gatewayDto.getId(),
                gatewayDto.getStart(),
                gatewayDto.getEnd(),
                gatewayDto.getItemId(),
                gatewayDto.getBookerId(),
                gatewayDto.getStatus()
        );
    }

    public BookingDto toGatewayBookingDto(ru.practicum.shareit.server.booking.dto.BookingDto serverDto) {
        if (serverDto == null) {
            return null;
        }
        return new BookingDto(
                serverDto.getId(),
                serverDto.getStart(),
                serverDto.getEnd(),
                serverDto.getItemId(),
                serverDto.getBookerId(),
                serverDto.getStatus()
        );
    }

    // Comment конвертеры
    public ru.practicum.shareit.server.item.dto.CommentDto toServerCommentDto(CommentDto gatewayDto) {
        if (gatewayDto == null) {
            return null;
        }
        return new ru.practicum.shareit.server.item.dto.CommentDto(
                gatewayDto.getText()
        );
    }

    // Request конвертеры
    public ru.practicum.shareit.server.request.dto.ItemRequestDto toServerRequestDto(ItemRequestDto gatewayDto) {
        if (gatewayDto == null) {
            return null;
        }
        return new ru.practicum.shareit.server.request.dto.ItemRequestDto(
                gatewayDto.getId(),
                gatewayDto.getDescription(),
                gatewayDto.getRequestorId(),
                gatewayDto.getCreated()
        );
    }

    public ItemRequestDto toGatewayRequestDto(ru.practicum.shareit.server.request.dto.ItemRequestDto serverDto) {
        if (serverDto == null) {
            return null;
        }
        return new ItemRequestDto(
                serverDto.getId(),
                serverDto.getDescription(),
                serverDto.getRequestorId(),
                serverDto.getCreated()
        );
    }

    public ItemRequestWithItemsDto toGatewayRequestWithItemsDto(
            ru.practicum.shareit.server.request.dto.ItemRequestWithItemsDto serverDto) {
        if (serverDto == null) {
            return null;
        }
        return new ItemRequestWithItemsDto(
                serverDto.getId(),
                serverDto.getDescription(),
                serverDto.getRequestorId(),
                serverDto.getCreated(),
                serverDto.getItems() != null ? serverDto.getItems().stream()
                        .map(this::toGatewayItemForRequestDto)
                        .collect(java.util.stream.Collectors.toList())
                        : null
        );
    }

    public ItemForRequestDto toGatewayItemForRequestDto(
            ru.practicum.shareit.server.item.dto.ItemForRequestDto serverDto) {
        if (serverDto == null) {
            return null;
        }
        return new ItemForRequestDto(
                serverDto.getId(),
                serverDto.getName(),
                serverDto.getDescription(),
                serverDto.getAvailable(),
                serverDto.getOwnerId(),
                serverDto.getRequestId()
        );
    }
}