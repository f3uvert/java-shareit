package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.validation.NotBlankOrNull;

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
}