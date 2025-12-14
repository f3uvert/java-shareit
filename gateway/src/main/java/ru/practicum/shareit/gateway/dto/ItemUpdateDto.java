package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.gateway.validation.NotBlankOrNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDto {
    private Long id;

    @NotBlankOrNull(message = "Name must be not blank or null")
    private String name;

    @NotBlankOrNull(message = "Description must be not blank or null")
    private String description;

    private Boolean available;

    private Long requestId;

    @AssertTrue(message = "At least one field must be provided for update")
    private boolean isAnyFieldProvided() {
        return name != null || description != null || available != null;
    }
}