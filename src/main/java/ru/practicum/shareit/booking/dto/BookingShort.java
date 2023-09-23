package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Value;

public interface BookingShort {

    long getId();

    @Value("#{target.booker.id}")
    long getBookerId();
}
