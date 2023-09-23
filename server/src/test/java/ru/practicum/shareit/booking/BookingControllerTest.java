package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.user.dto.UserShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.DATE_TIME_FORMATTER;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    BookingService bookingService;

    private CreateBookingRequest createBookingRequest;
    private CreateBookingRequest createBookingRequestWithNullItemId;
    private CreateBookingRequest createBookingRequestWithNullStart;
    private CreateBookingRequest createBookingRequestWithNullEnd;
    private CreateBookingRequest createBookingRequestWithStartInPast;
    private CreateBookingRequest createBookingRequestWithStartInPresent;
    private CreateBookingRequest createBookingRequestWithStartAfterEnd;
    private CreateBookingRequest createBookingRequestWithStartEqualEnd;
    private BookingResponse bookingResponse;

    @BeforeEach
    void beforeEach() {
        createBookingRequest = new CreateBookingRequest(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        createBookingRequestWithNullItemId = new CreateBookingRequest(null,
                createBookingRequest.getStart(),
                createBookingRequest.getEnd());

        createBookingRequestWithNullStart = new CreateBookingRequest(1L,
                null,
                createBookingRequest.getEnd());

        createBookingRequestWithNullEnd = new CreateBookingRequest(1L,
                createBookingRequest.getStart(),
                null);

        createBookingRequestWithStartInPast = new CreateBookingRequest(1L,
                LocalDateTime.now().minusDays(1),
                createBookingRequest.getEnd());

        createBookingRequestWithStartInPresent = new CreateBookingRequest(1L,
                LocalDateTime.now(),
                createBookingRequest.getEnd());

        createBookingRequestWithStartAfterEnd = new CreateBookingRequest(1L,
                createBookingRequest.getEnd().plusDays(1),
                createBookingRequest.getEnd());

        createBookingRequestWithStartEqualEnd = new CreateBookingRequest(1L,
                createBookingRequest.getEnd(),
                createBookingRequest.getEnd());

        bookingResponse = new BookingResponse(1L,
                createBookingRequest.getStart(),
                createBookingRequest.getEnd(),
                Status.APPROVED,
                new UserShort().setId(1L),
                new ItemShort(1L, "Дрель"));
    }

    @Test
    void givenCorrectBookingDto_whenSave_thenReturnAnotherBookingDto() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequest))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponse.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponse.getItem().getName()));
    }

    @Test
    void givenNullItemId_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestWithNullItemId))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenNullStart_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestWithNullStart))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenStartInPast_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestWithStartInPast))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenStartInPresent_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestWithStartInPresent))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenNullEnd_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestWithNullEnd))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenStartAfterEnd_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestWithStartAfterEnd))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenStartEqualEnd_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequestWithStartEqualEnd))
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenNoUserIdHeader_whenSave_thenThrowException() throws Exception {
        when(bookingService.save(anyLong(), any(CreateBookingRequest.class)))
                .thenReturn(bookingResponse);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenCorrectBookingIdAndUserIdAndApproved_whenUpdate_thenReturnBookingDto() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponse.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingResponse.getItem().getId()));
    }

    @Test
    void givenNullPathVariable_whenUpdate_thenThrowException() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/null")
                        .header(USER_ID_HEADER, 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenNoUserIdHeader_whenUpdate_thenThrowException() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenNoApproveParam_whenUpdate_thenThrowException() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenNullApproveParam_whenUpdate_thenThrowException() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "null")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenCorrectBookingIdAndUserId_whenFindByIdOrUserId_thenReturnBookingDto() throws Exception {
        when(bookingService.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponse.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.end").value(bookingResponse.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$.booker.id").value(bookingResponse.getBooker().getId()))
                .andExpect(jsonPath("$.item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingResponse.getItem().getName()));
    }

    @Test
    void givenNullPathVariable_whenFindByIdOrUserId_thenThrowException() throws Exception {
        when(bookingService.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(get("/bookings/null")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenNoUserIdHeader_whenFindByIdOrUserId_thenThrowException() throws Exception {
        when(bookingService.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(bookingResponse);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenCorrectBookerIdAndStateWithoutFromAndSize_whenFindByBookerIdAndState_thenReturnListOfBookingDto() throws Exception {
        when(bookingService.findByBookerIdAndState(anyLong(), anyString(), anyLong(), anyInt()))
                .thenReturn(List.of(bookingResponse, bookingResponse));

        mvc.perform(get("/bookings/")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingResponse.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].end").value(bookingResponse.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$[0].booker.id").value(bookingResponse.getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookingResponse.getItem().getName()));
    }

    @Test
    void givenNoBookerId_whenFindByBookerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenFromLessThan0_whenFindByBookerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenFromMoreThanMaxOfLong_whenFindByBookerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(Long.MAX_VALUE + 1))
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenSizeLessThan1_whenFindByBookerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenSizeMoreThan100_whenFindByBookerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "101")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenCorrectOwnerIdAndStateWithoutFromAndSize_whenFindByItemOwnerIdAndState_thenReturnListOfBookingDto()
            throws Exception {
        when(bookingService.findByItemOwnerIdAndState(anyLong(), anyString(), anyLong(), anyInt()))
                .thenReturn(List.of(bookingResponse, bookingResponse));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(bookingResponse.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingResponse.getStart().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].end").value(bookingResponse.getEnd().format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].status").value(bookingResponse.getStatus().toString()))
                .andExpect(jsonPath("$[0].booker.id").value(bookingResponse.getBooker().getId()))
                .andExpect(jsonPath("$[0].item.id").value(bookingResponse.getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookingResponse.getItem().getName()));
    }

    @Test
    void givenNoBookerId_whenFindByItemOwnerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenFromLessThan0_whenFindByItemOwnerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenFromMoreThanMaxOfLong_whenFindByItemOwnerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", String.valueOf(Long.MAX_VALUE + 1))
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenSizeLessThan1_whenFindByItemOwnerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void givenSizeMoreThan100_whenFindByItemOwnerIdAndState_thenThrowException() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID_HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "101")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}