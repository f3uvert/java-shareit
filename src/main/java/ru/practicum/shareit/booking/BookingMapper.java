package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto.BookingUserDto;

@UtilityClass
public class BookingMapper {

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingItemDto itemDto = new BookingItemDto(
                booking.getItem().getId(),
                booking.getItem().getName()
        );

        BookingUserDto userDto = new BookingUserDto(
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