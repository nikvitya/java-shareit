package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.shareit.booking.dto.BookingDtoForGetItemResponse;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@UtilityClass
public class BookingMapper {
    public static GetBookingDto toGetBookingDto(Booking booking) {
        return new GetBookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                UserMapper.toUserDtoForBooking(booking.getBooker()),
                ItemMapper.toItemDtoForBooking(booking.getItem()));
    }

    public static BookingDtoForGetItemResponse toBookingDtoForGetItemResponse(Booking booking) {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return factory.createProjection(BookingDtoForGetItemResponse.class, booking);
    }
}