package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchItemResponse {

    private long id;
    private String name;
    private String description;
    private boolean available;
}
