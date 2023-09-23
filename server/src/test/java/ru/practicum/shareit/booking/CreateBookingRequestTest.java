package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateBookingRequestTest {
    @Autowired
    private JacksonTester<BookingResponse> json;

    @Test
    @DisplayName("Протестировать заказ, возвращаемый с сервера на фронтэнд")
    void testBookingResponse() throws Exception {
        BookingResponse bookingResponse = new BookingResponse(1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                Status.APPROVED,
                new UserShort().setId(1L),
                new ItemShort(1L, "Дрель"));

        JsonContent<BookingResponse> result = json.write(bookingResponse);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingResponse.getStatus().toString());
        assertThat(result).hasJsonPathValue("$.booker");
        assertThat(result).hasJsonPathValue("$.item");
    }
}