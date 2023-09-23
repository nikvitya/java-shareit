package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.exception.*;
//import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {

    private static BookingService bookingService;
    private static BookingService bookingServiceSpy;
    private static User user;
    private static User user2;
    private static User user3;
    private static String itemName;
    private static String itemDescription;
    private static Item item;
    private static LocalDateTime start;
    private static LocalDateTime end;
    private static CreateBookingRequest createBookingRequest;
    private static Long bookingId;
    private static Booking booking;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);
        bookingServiceSpy = spy(bookingService);
    }

    @BeforeAll
    static void beforeAll() {
        user = new User()
                .setId(1L)
                .setName("Игорь")
                .setEmail("igor@mail.ru");

        user2 = new User()
                .setId(2L)
                .setName("Гоша")
                .setEmail("Gosha@mail.ru");

        user3 = new User()
                .setId(3L)
                .setName("Павел")
                .setEmail("pavel@mail.ru");

        itemName = "Дрель";
        itemDescription = "Ударная 20V";

        item = new Item()
                .setId(1L)
                .setName(itemName)
                .setDescription(itemDescription)
                .setAvailable(true)
                .setOwner(user);

        start = LocalDateTime.now().minusDays(2);
        end = LocalDateTime.now().plusDays(2);

        createBookingRequest = new CreateBookingRequest(
                item.getId(),
                start,
                end
        );

        bookingId = 1L;

        booking = new Booking()
                .setId(bookingId)
                .setStart(start)
                .setEnd(end)
                .setStatus(Status.WAITING)
                .setBooker(user2)
                .setItem(item);
    }

    @Test
    void givenCorrectBookerIdAndBookingDto_whenCreateBooking_thenReturnAnotherBookingDto() {
        when(itemService.findById(anyLong()))
                .thenReturn(item);
        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingResponse newBookingResponse = bookingService.save(user2.getId(), createBookingRequest);

        assertThat(newBookingResponse.getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse.getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse.getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse.getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse.getItem().getName(), equalTo(booking.getItem().getName()));
        verify(itemService, times(1)).findById(anyLong());
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenNonExistentBookerId_whenCreateBooking_thenThrowException() {
        when(itemService.findById(anyLong()))
                .thenReturn(item);
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        lenient().when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.save(99L, createBookingRequest));
        verify(itemService, times(1)).findById(anyLong());
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenBookingDtoWithNonExistentItemId_whenCreateBooking_thenThrowException() {
        when(itemService.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        lenient().when(userService.findById(anyLong()))
                .thenReturn(user2);
        lenient().when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.save(user2.getId(),
                new CreateBookingRequest(99L, start, end)));
        verify(itemService, times(1)).findById(anyLong());
        verify(userService, times(0)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenBookingDtoOwnerIdEqualBookerId_whenCreateBooking_thenThrowException() {
        when(itemService.findById(anyLong()))
                .thenReturn(new Item()
                        .setId(2L)
                        .setName(itemName)
                        .setDescription(itemDescription)
                        .setAvailable(true)
                        .setOwner(user2));
        when(userService.findById(anyLong()))
                .thenReturn(user2);
        lenient().when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        assertThrows(WrongUserIdException.class, () -> bookingService.save(user2.getId(),
                new CreateBookingRequest(2L, start, end)));
        verify(itemService, times(1)).findById(anyLong());
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenBookingDtoWithNotAvailable_whenCreateBooking_thenThrowException() {
        when(itemService.findById(anyLong()))
                .thenReturn(new Item()
                        .setId(2L)
                        .setName(itemName)
                        .setDescription(itemDescription)
                        .setAvailable(false)
                        .setOwner(user));
        when(userService.findById(anyLong()))
                .thenReturn(user2);
        lenient().when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        assertThrows(ItemNotAvailableException.class, () -> bookingService.save(user2.getId(), createBookingRequest));
        verify(itemService, times(1)).findById(anyLong());
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectBookingIdAndOwnerIdAndApproved_whenUpdateBookingStatus_thenReturnUpdatedBookingDto() {
        doReturn(booking).when(bookingServiceSpy).findById(anyLong());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(new Booking()
                        .setId(booking.getId())
                        .setStart(booking.getStart())
                        .setEnd(booking.getEnd())
                        .setStatus(Status.APPROVED)
                        .setBooker(booking.getBooker())
                        .setItem(booking.getItem()));

        BookingResponse newBookingResponse = bookingServiceSpy.update(bookingId, booking.getItem().getOwner().getId(),
                true);
        assertThat(newBookingResponse.getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse.getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse.getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse.getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse.getItem().getName(), equalTo(booking.getItem().getName()));
        verifyNoInteractions(itemService, userService);
        verify(bookingServiceSpy, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingServiceSpy, times(1)).update(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(bookingServiceSpy, bookingRepository);
    }

    @Test
    void givenNonExistentBookingId_whenUpdateBookingStatus_thenThrowException() {
        doThrow(NotFoundException.class).when(bookingServiceSpy).findById(anyLong());
        lenient().when(bookingRepository.save(any(Booking.class)))
                .thenReturn(new Booking()
                        .setId(booking.getId())
                        .setStart(booking.getStart())
                        .setEnd(booking.getEnd())
                        .setStatus(Status.APPROVED)
                        .setBooker(booking.getBooker())
                        .setItem(booking.getItem()));

        assertThrows(NotFoundException.class, () -> bookingServiceSpy.update(99L, booking.getBooker().getId(),
                true));
        verify(bookingServiceSpy, times(1)).findById(anyLong());
        verify(bookingServiceSpy, times(1)).update(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(itemService, userService, bookingRepository, bookingServiceSpy);
    }

    @Test
    void givenOwnerIdNotEqualBookingItemOwnerId_whenUpdateBookingStatus_thenThrowException() {
        doReturn(booking).when(bookingServiceSpy).findById(anyLong());
        lenient().when(bookingRepository.save(any(Booking.class)))
                .thenReturn(new Booking()
                        .setId(booking.getId())
                        .setStart(booking.getStart())
                        .setEnd(booking.getEnd())
                        .setStatus(Status.APPROVED)
                        .setBooker(booking.getBooker())
                        .setItem(booking.getItem()));

        assertThrows(WrongUserIdException.class, () -> bookingServiceSpy.update(bookingId, user3.getId(), true));
        verify(bookingServiceSpy, times(1)).findById(anyLong());
        verify(bookingServiceSpy, times(1)).update(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(itemService, userService, bookingRepository, bookingServiceSpy);
    }

    @Test
    void givenBookingStatusNotEqualWaiting_whenUpdateBookingStatus_thenThrowException() {
        doReturn(new Booking()
                .setId(booking.getId())
                .setStart(booking.getStart())
                .setEnd(booking.getEnd())
                .setStatus(Status.REJECTED)
                .setBooker(booking.getBooker())
                .setItem(booking.getItem()))
                .when(bookingServiceSpy).findById(anyLong());
        lenient().when(bookingRepository.save(any(Booking.class)))
                .thenReturn(new Booking()
                        .setId(booking.getId())
                        .setStart(booking.getStart())
                        .setEnd(booking.getEnd())
                        .setStatus(Status.APPROVED)
                        .setBooker(booking.getBooker())
                        .setItem(booking.getItem()));

        assertThrows(StatusAlreadyChangedException.class, () -> bookingServiceSpy.update(bookingId,
                booking.getItem().getOwner().getId(),
                true));
        verify(bookingServiceSpy, times(1)).findById(anyLong());
        verify(bookingServiceSpy, times(1)).update(anyLong(), anyLong(), anyBoolean());

        doReturn(new Booking()
                .setId(booking.getId())
                .setStart(booking.getStart())
                .setEnd(booking.getEnd())
                .setStatus(Status.APPROVED)
                .setBooker(booking.getBooker())
                .setItem(booking.getItem()))
                .when(bookingServiceSpy).findById(anyLong());

        assertThrows(StatusAlreadyChangedException.class, () -> bookingServiceSpy.update(bookingId,
                booking.getItem().getOwner().getId(),
                true));
        verify(bookingServiceSpy, times(2)).findById(anyLong());
        verify(bookingServiceSpy, times(2)).update(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(itemService, userService, bookingRepository, bookingServiceSpy);
    }

    @Test
    void givenCorrectBookingIdAndUserId_whenFindByIdAndUserId_thenReturnBookingDto() {
        doReturn(booking).when(bookingServiceSpy).findById(anyLong());

        BookingResponse newBookingResponse = bookingServiceSpy.findByIdAndUserId(bookingId,
                booking.getBooker().getId());
        assertThat(newBookingResponse.getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse.getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse.getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse.getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse.getItem().getName(), equalTo(booking.getItem().getName()));
        verify(bookingServiceSpy, times(1)).findById(anyLong());
        verify(bookingServiceSpy, times(1)).findByIdAndUserId(anyLong(), anyLong());

        BookingResponse newBookingResponse2 = bookingServiceSpy.findByIdAndUserId(bookingId,
                booking.getItem().getOwner().getId());
        assertThat(newBookingResponse2.getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse2.getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse2.getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse2.getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse2.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse2.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse2.getItem().getName(), equalTo(booking.getItem().getName()));
        verify(bookingServiceSpy, times(2)).findById(anyLong());
        verify(bookingServiceSpy, times(2)).findByIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository, bookingServiceSpy);
    }

    @Test
    void givenNonExistentBookingId_whenFindByIdAndUserId_thenThrowException() {
        doThrow(NotFoundException.class).when(bookingServiceSpy).findById(anyLong());

        assertThrows(NotFoundException.class, () -> bookingServiceSpy.findByIdAndUserId(99L,
                booking.getBooker().getId()));
        verify(bookingServiceSpy, times(1)).findById(anyLong());
        verify(bookingServiceSpy, times(1)).findByIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository, bookingServiceSpy);
    }

    @Test
    void givenUserIdNotEqualOwnerIdAndBookerId_whenFindByIdAndUserId_thenThrowException() {
        doReturn(booking).when(bookingServiceSpy).findById(anyLong());

        assertThrows(WrongUserIdException.class, () -> bookingServiceSpy.findByIdAndUserId(bookingId, user3.getId()));
        verify(bookingServiceSpy, times(1)).findById(anyLong());
        verify(bookingServiceSpy, times(1)).findByIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository, bookingServiceSpy);
    }

    @Test
    void givenCorrectBookerIdAndStateAll_whenFindByBookerIdAndState_thenReturnListOfBookingDto() {
        String state = "All";
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));

        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse = bookingService.findByBookerIdAndState(booking.getBooker().getId(),
                state, 0L, 20);
        assertThat(1, equalTo(newBookingResponse.size()));
        assertThat(newBookingResponse.get(0).getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse.get(0).getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse.get(0).getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse.get(0).getItem().getName(), equalTo(booking.getItem().getName()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectBookerIdAndStateCurrent_whenFindByBookerIdAndState_thenReturnListOfBookingDto() {
        String state = "CURRENT";
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));

        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse =
                bookingService.findByBookerIdAndState(booking.getBooker().getId(), state, 0L, 20);
        assertThat(1, equalTo(newBookingResponse.size()));
        assertThat(newBookingResponse.get(0).getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse.get(0).getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse.get(0).getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse.get(0).getItem().getName(), equalTo(booking.getItem().getName()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(anyLong(),
                        any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectBookerIdAndStatePast_whenFindByBookerIdAndState_thenReturnListOfBookingDto() {
        String state = "PAST";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse =
                bookingService.findByBookerIdAndState(booking.getBooker().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectBookerIdAndStateFuture_whenFindByBookerIdAndState_thenReturnListOfBookingDto() {
        String state = "FUTURE";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse =
                bookingService.findByBookerIdAndState(booking.getBooker().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectBookerIdAndStateWaiting_whenFindByBookerIdAndState_thenReturnListOfBookingDto() {
        String state = "WAITING";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class),
                any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse =
                bookingService.findByBookerIdAndState(booking.getBooker().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectBookerIdAndStateRejected_whenFindByBookerIdAndState_thenReturnListOfBookingDto() {
        String state = "REJEcTED";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class),
                any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse =
                bookingService.findByBookerIdAndState(booking.getBooker().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenNonExistentBookerId_whenFindByBookerIdAndState_thenThrowException() {
        String state = "REJECTED";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        lenient().when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class),
                        any(Pageable.class)))
                .thenReturn(bookingPage);

        assertThrows(NotFoundException.class, () ->
                bookingService.findByBookerIdAndState(99L, state, 0L, 20));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(0))
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(Status.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenUnsupportedState_whenFindByBookerIdAndState_thenThrowException() {
        String state = "ANY";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        assertThrows(UnknownStateException.class, () ->
                bookingService.findByBookerIdAndState(booking.getBooker().getId(), state, 0L, 20));
        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectItemOwnerIdAndStateAll_whenFindByItemOwnerIdAndState_thenReturnListOfBookingDto() {
        String state = "ALL";
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));

        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse = bookingService
                .findByItemOwnerIdAndState(booking.getItem().getOwner().getId(), state, 0L, 20);
        assertThat(1, equalTo(newBookingResponse.size()));
        assertThat(newBookingResponse.get(0).getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse.get(0).getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse.get(0).getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse.get(0).getItem().getName(), equalTo(booking.getItem().getName()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerId(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectItemOwnerIdAndStateCurrent_whenFindByItemOwnerIdAndState_thenReturnListOfBookingDto() {
        String state = "CURRENT";
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));

        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse = bookingService
                .findByItemOwnerIdAndState(booking.getItem().getOwner().getId(), state, 0L, 20);
        assertThat(1, equalTo(newBookingResponse.size()));
        assertThat(newBookingResponse.get(0).getId(), equalTo(booking.getId()));
        assertThat(newBookingResponse.get(0).getStart(), equalTo(booking.getStart()));
        assertThat(newBookingResponse.get(0).getEnd(), equalTo(booking.getEnd()));
        assertThat(newBookingResponse.get(0).getStatus(), equalTo(booking.getStatus()));
        assertThat(newBookingResponse.get(0).getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(newBookingResponse.get(0).getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(newBookingResponse.get(0).getItem().getName(), equalTo(booking.getItem().getName()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectItemOwnerIdAndStatePast_whenFindByItemOwnerIdAndState_thenReturnListOfBookingDto() {
        String state = "PAST";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse = bookingService
                .findByItemOwnerIdAndState(booking.getItem().getOwner().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectItemOwnerIdAndStateFuture_whenFindByItemOwnerIdAndState_thenReturnListOfBookingDto() {
        String state = "FUTURE";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse = bookingService
                .findByItemOwnerIdAndState(booking.getItem().getOwner().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectItemOwnerIdAndStateWaiting_whenFindByItemOwnerIdAndState_thenReturnListOfBookingDto() {
        String state = "WAItING";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse = bookingService
                .findByItemOwnerIdAndState(booking.getItem().getOwner().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectItemOwnerIdAndStateRejected_whenFindByItemOwnerIdAndState_thenReturnListOfBookingDto() {
        String state = "REJECTED";
        Page<Booking> bookingPage = new PageImpl<>(Collections.emptyList());

        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(bookingPage);

        List<BookingResponse> newBookingResponse = bookingService
                .findByItemOwnerIdAndState(booking.getItem().getOwner().getId(), state, 0L, 20);
        assertThat(0, equalTo(newBookingResponse.size()));
        verify(userService, times(1)).findById(anyLong());
        verify(bookingRepository, times(1))
                .findByItemOwnerIdAndStatus(anyLong(), any(Status.class), any(Pageable.class));
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenUnsupportedState_whenFindByItemOwnerIdAndState_thenThrowException() {
        String state = "SomE";
        when(userService.findById(anyLong()))
                .thenReturn(user);

        assertThrows(UnknownStateException.class, () -> bookingService
                .findByItemOwnerIdAndState(booking.getItem().getOwner().getId(), state, 0L, 20));
        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenNonExistentOwnerId_whenFindByItemOwnerIdAndState_thenThrowException() {
        String state = "CURRENT";
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService
                .findByItemOwnerIdAndState(99L, state, 0L, 20));
        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenCorrectBookingId_whenFindById_thenReturnBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking foundBooking = bookingService.findById(1L);

        assertThat(foundBooking.getId(), equalTo(booking.getId()));
        assertThat(foundBooking.getStart(), equalTo(booking.getStart()));
        assertThat(foundBooking.getEnd(), equalTo(booking.getEnd()));
        assertThat(foundBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(foundBooking.getBooker(), equalTo(booking.getBooker()));
        assertThat(foundBooking.getItem(), equalTo(booking.getItem()));
        verify(bookingRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }

    @Test
    void givenNonExistentBookingId_whenFindById_thenThrowException() {
        when(bookingRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.findById(1L));
        verify(bookingRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(itemService, userService, bookingRepository);
    }
}