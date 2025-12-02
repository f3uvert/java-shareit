package ru.practicum.shareit.gateway.validation;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NoSuchElementException e) {
        log.error("Not found: {}", e.getMessage());
        return new ErrorResponse("Not found", e.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(SecurityException e) {
        log.error("Forbidden: {}", e.getMessage());
        return new ErrorResponse("Forbidden", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException e) {
        log.error("Conflict: {}", e.getMessage());
        return new ErrorResponse("Conflict", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(ValidationException e) {
        log.error("Validation error: {}", e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        log.error("Validation error: {}", errorMessage);
        return new ErrorResponse("Validation error", errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Constraint violation");
        log.error("Constraint violation: {}", errorMessage);
        return new ErrorResponse("Constraint violation", errorMessage);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(MissingRequestHeaderException ex) {
        log.error("Missing header: {}", ex.getMessage());
        return new ErrorResponse("Missing header", "Required header 'X-Sharer-User-Id' is missing");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Type mismatch: {}", ex.getMessage());
        return new ErrorResponse("Invalid parameter", "Invalid parameter type: " + ex.getName());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Data integrity violation: {}", e.getMessage());

        String message = e.getMessage();
        if (message != null) {
            if (message.contains("foreign key constraint") || message.contains("REFERENCES")) {
                return new ErrorResponse("Data integrity violation",
                        "Cannot delete because of existing references in other tables");
            } else if (message.contains("unique constraint") || message.contains("duplicate key")) {
                return new ErrorResponse("Data integrity violation", "Duplicate entry");
            }
        }

        return new ErrorResponse("Data integrity violation", "Data integrity violation");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(Exception e) {
        log.error("Internal server error: ", e);
        return new ErrorResponse("Internal server error",
                e.getMessage() != null ? e.getMessage() : "An unexpected error occurred");
    }
}