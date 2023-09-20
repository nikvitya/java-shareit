package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constants.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
public class CreateRequestResponse {
    private Long id;
    private String description;
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;
}