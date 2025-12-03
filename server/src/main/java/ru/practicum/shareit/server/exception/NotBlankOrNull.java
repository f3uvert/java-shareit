package ru.practicum.shareit.server.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankOrNullValidator.class)
public @interface NotBlankOrNull {
    String message() default "Field must be not blank or null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}