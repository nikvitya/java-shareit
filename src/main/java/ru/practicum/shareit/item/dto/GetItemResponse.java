package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoForGetItemResponse;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetItemResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoForGetItemResponse lastBooking;
    private BookingDtoForGetItemResponse nextBooking;
    private List<CommentResponse> comments;
}
