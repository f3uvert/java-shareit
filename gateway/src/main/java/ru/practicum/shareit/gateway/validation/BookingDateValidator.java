package ru.practicum.shareit.gateway.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.gateway.dto.BookingDto;

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

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            context.buildConstraintViolationWithTemplate("Start date must be in present or future")
                    .addPropertyNode("start")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}