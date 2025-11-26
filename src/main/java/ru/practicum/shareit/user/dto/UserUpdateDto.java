package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.exception.NotBlankOrNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private Long id;

    @NotBlankOrNull(message = "Name must be not blank or null")
    private String name;

    @NotBlankOrNull(message = "Email must be not blank or null")
    @Email(message = "Email should be valid")
    private String email;

    @AssertTrue(message = "At least one field must be provided for update")
    private boolean isAnyFieldProvided() {
        return name != null || email != null;
    }
}