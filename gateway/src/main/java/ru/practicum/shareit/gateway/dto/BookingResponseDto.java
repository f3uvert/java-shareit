package ru.practicum.shareit.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingItemDto item;
    private BookingUserDto booker;
    private BookingStatus status;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingItemDto {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingUserDto {
        private Long id;
        private String name;
    }
}