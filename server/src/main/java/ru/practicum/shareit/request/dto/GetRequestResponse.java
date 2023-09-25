package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemResponse;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.shareit.util.Constants.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
public class GetRequestResponse {
    private Long id;
    private String description;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
    private List<ItemResponse> items;
}