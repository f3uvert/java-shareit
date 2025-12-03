package ru.practicum.shareit.server.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingDateValidator.class)
public @interface ValidBookingDate {
    String message() default "Invalid booking dates";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}