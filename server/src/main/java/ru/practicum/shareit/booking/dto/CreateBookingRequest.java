package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constants.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
public class CreateBookingRequest {

    private Long itemId;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;
}