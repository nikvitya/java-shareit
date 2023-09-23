package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constants.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
public class BookingResponse {
    private long id;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;

    private Status status;
    private UserShort booker;
    private ItemShort item;
}
