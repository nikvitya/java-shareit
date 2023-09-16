package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Value;

public interface BookingDtoForGetItemResponse {

    long getId();

    @Value("#{target.booker.id}")
    long getBookerId();
}
