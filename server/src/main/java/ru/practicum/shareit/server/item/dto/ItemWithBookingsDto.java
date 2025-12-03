package ru.practicum.shareit.server.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingInfoDto lastBooking;
    private BookingInfoDto nextBooking;
    private List<CommentResponseDto> comments;
    private Long requestId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfoDto {
        private Long id;
        private Long bookerId;
    }
}