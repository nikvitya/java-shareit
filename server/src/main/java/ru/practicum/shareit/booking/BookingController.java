package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse save(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                @RequestBody @Valid CreateBookingRequest createBookingRequest) {
        return bookingService.save(bookerId, createBookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse update(@PathVariable Long bookingId,
                                  @RequestHeader(USER_ID_HEADER) Long ownerId,
                                  @RequestParam boolean approved) {
        return bookingService.update(bookingId, ownerId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingResponse findByIdAndUserId(@PathVariable Long bookingId,
                                             @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingService.findByIdAndUserId(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponse> findByBookerIdAndState(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return bookingService.findByBookerIdAndState(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> findByItemOwnerIdAndState(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                           @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return bookingService.findByItemOwnerIdAndState(ownerId, state, from, size);
    }
}