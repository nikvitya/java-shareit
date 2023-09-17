package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    GetBookingDto save(CreateBookingDto createBookingDto, Long bookerId);

    GetBookingDto update(Long bookingId, Long ownerId, boolean approved);

    GetBookingDto findByIdAndOwnerOrBookerId(Long bookingId, Long ownerOrBookerId);

    List<Booking> findByBookerIdAndState(Long bookerId, String state);

    List<Booking> findByItemOwnerIdAndState(Long ownerId, String state);
}