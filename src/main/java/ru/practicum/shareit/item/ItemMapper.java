package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoForGetItemResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public static CreateItemResponse toCreateItemResponse(Item item) {
        return new CreateItemResponse(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static ItemDtoForGetBookingDto toItemDtoForBooking(Item item) {
        return new ItemDtoForGetBookingDto(item.getId(), item.getName());
    }

    public static GetItemResponse toGetItemResponse(Item item,
                                                    BookingDtoForGetItemResponse lastBooking,
                                                    BookingDtoForGetItemResponse nextBooking,
                                                    List<CommentResponse> commentResponse) {
        return new GetItemResponse(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking,
                nextBooking,
                commentResponse);
    }

    public static GetItemResponse toGetItemResponse(Item item) {
        Booking lastBookingNotDto = item.getBookings().stream().filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart)).orElse(null);
        Booking nextBookingNotDto = item.getBookings().stream().filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        return new GetItemResponse(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBookingNotDto == null ? null : BookingMapper.toBookingDtoForGetItemResponse(lastBookingNotDto),
                nextBookingNotDto == null ? null : BookingMapper.toBookingDtoForGetItemResponse(nextBookingNotDto),
                item.getComments().stream().map(CommentMapper::toCommentResponse).collect(Collectors.toList()));
    }

    public static Item toItem(User user, CreateItemRequest createItemRequest) {
        return new Item()
                .setName(createItemRequest.getName())
                .setDescription(createItemRequest.getDescription())
                .setAvailable(createItemRequest.getAvailable())
                .setOwner(user);
    }
}