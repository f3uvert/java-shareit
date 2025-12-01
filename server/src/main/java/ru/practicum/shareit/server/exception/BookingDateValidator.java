package ru.practicum.shareit.server.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.server.booking.dto.BookingDto;


import java.time.LocalDateTime;

public class BookingDateValidator implements ConstraintValidator<ValidBookingDate, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            return true;
        }

        boolean isValid = true;

        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            context.buildConstraintViolationWithTemplate("End date must be after start date")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            isValid = false;
        }

        if (!bookingDto.getStart().isAfter(LocalDateTime.now())) {
            context.buildConstraintViolationWithTemplate("Start date must be in future")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            isValid = false;
        }

        if (bookingDto.getEnd().isBefore(LocalDateTime.now().plusHours(1))) {
            context.buildConstraintViolationWithTemplate("Booking must be at least 1 hour long")
                    .addPropertyNode("end")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}