package ru.practicum.shareit.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestWithItemsDto {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}