package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking save(Booking booking);

    Booking update(Long bookingId, Long ownerId, boolean approved);

    Booking findByIdAndOwnerOrBookerId(Long bookingId, Long ownerOrBookerId);

    List<Booking> findByBookerIdAndState(Long bookerId, String state);

    List<Booking> findByItemOwnerIdAndState(Long ownerId, String state);
}