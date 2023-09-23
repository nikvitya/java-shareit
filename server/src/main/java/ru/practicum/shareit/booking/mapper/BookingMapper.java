package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {
    public static BookingResponse toBookingResponse(Booking booking) {
        return new BookingResponse(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDtoShort(booking.getBooker()),
                ItemMapper.toItemShort(booking.getItem()));
    }

    public static BookingShort toBookingDtoShort(Booking booking) {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return factory.createProjection(BookingShort.class, booking);
    }
}