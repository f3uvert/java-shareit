package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotBlank(message = "Comment text cannot be blank")
    @Size(min = 1, max = 1000, message = "Comment must be between 1 and 1000 characters")
    private String text;
}