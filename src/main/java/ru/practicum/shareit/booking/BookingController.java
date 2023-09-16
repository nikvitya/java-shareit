package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public GetBookingDto save(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                              @RequestBody @Valid CreateBookingDto createBookingDto) {
        return BookingMapper.toGetBookingDto(bookingService.save(
                new Booking()
                        .setStart(createBookingDto.getStart())
                        .setEnd(createBookingDto.getEnd())
                        .setStatus(Status.WAITING)
                        .setBooker(userService.findById(bookerId))
                        .setItem(itemService.findById(createBookingDto.getItemId()))
        ));
    }

    @PatchMapping("/{bookingId}")
    public GetBookingDto update(@PathVariable Long bookingId,
                                @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                @RequestParam boolean approved) {
        return BookingMapper.toGetBookingDto(bookingService.update(bookingId, ownerId, approved));
    }

    @GetMapping("{bookingId}")
    public GetBookingDto findByIdAndOwnerOrBookerId(@PathVariable Long bookingId,
                                                    @RequestHeader("X-Sharer-User-Id") Long ownerOrBookerId) {
        return BookingMapper.toGetBookingDto(bookingService.findByIdAndOwnerOrBookerId(bookingId, ownerOrBookerId));
    }

    @GetMapping
    public List<GetBookingDto> findByBookerIdAndState(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByBookerIdAndState(bookerId, state).stream()
                .map(BookingMapper::toGetBookingDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<GetBookingDto> findByItemOwnerIdAndState(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findByItemOwnerIdAndState(ownerId, state).stream()
                .map(BookingMapper::toGetBookingDto).collect(Collectors.toList());
    }
}