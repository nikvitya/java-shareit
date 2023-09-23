package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                       @RequestBody @Valid CreateBookingRequest createBookingRequest) {
        log.info("Creating booking {}, userId={}", createBookingRequest, bookerId);
        return bookingClient.save(bookerId, createBookingRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable Long bookingId,
                                         @RequestHeader(USER_ID_HEADER) Long ownerId,
                                         @RequestParam boolean approved) {
        log.info("Update booking {}, ownerId={}, approved={}", bookingId, ownerId, approved);
        return bookingClient.update(bookingId, ownerId, approved);
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<Object> findByIdAndUserId(@PathVariable Long bookingId,
                                                    @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findByIdAndUserId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBookerIdAndState(@RequestHeader(USER_ID_HEADER) Long bookerId,
                                                         @RequestParam(name = "state", defaultValue = "all") String state,
                                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) Integer size) {
        BookingState validatedState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state {}, bookerId={}, from={}, size={}", state, bookerId, from, size);
        return bookingClient.findByBookerIdAndState(bookerId, validatedState, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByItemOwnerIdAndState(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                            @RequestParam(name = "state", defaultValue = "all") String state,
                                                            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                            @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size) {
        BookingState validatedState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", state, ownerId, from, size);
        return bookingClient.findByItemOwnerIdAndState(ownerId, validatedState, from, size);
    }
}