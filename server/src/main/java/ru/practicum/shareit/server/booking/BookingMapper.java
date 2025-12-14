package ru.practicum.shareit.server.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;

@UtilityClass
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingResponseDto.BookingItemDto itemDto = new BookingResponseDto.BookingItemDto(
                booking.getItem().getId(),
                booking.getItem().getName()
        );

        BookingResponseDto.BookingUserDto userDto = new BookingResponseDto.BookingUserDto(
                booking.getBooker().getId(),
                booking.getBooker().getName()
        );

        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                userDto,
                booking.getStatus()
        );
    }
}