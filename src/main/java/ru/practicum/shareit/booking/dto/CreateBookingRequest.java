package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.exception.customconstraint.StartBeforeEnd;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static ru.practicum.shareit.util.Constants.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
@StartBeforeEnd
public class CreateBookingRequest {

    @NotNull
    private Long itemId;

    @NotNull
    @Future
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime start;

    @NotNull
    @Future
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime end;
}
