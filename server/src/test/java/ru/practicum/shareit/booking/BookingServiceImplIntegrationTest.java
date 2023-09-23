package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@Rollback(value = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private static CreateUserRequest createUserRequest;
    private static CreateUserRequest createUserRequest2;
    private static CreateItemRequest createItemRequest;
    private static CreateBookingRequest createBookingRequest;

    @BeforeEach
    void beforeEach() {
        createUserRequest = new CreateUserRequest()
                .setName("Игорь")
                .setEmail("igor@mail.ru");

        createUserRequest2 = new CreateUserRequest()
                .setName("Павел")
                .setEmail("pavel@mail.ru");

        createItemRequest = new CreateItemRequest(
                "Дрель",
                "Ударная 20V",
                true,
                null);

        createBookingRequest = new CreateBookingRequest(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
    }

    @Test
    void save() {
        long ownerId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);

        TypedQuery<Booking> query = em.createQuery("select b from Booking as b where b.booker.id=:id", Booking.class);
        Booking booking = query.setParameter("id", bookerId).getSingleResult();

        assertThat(booking.getId(), equalTo(bookingId));
        assertThat(booking.getStart(), equalTo(createBookingRequest.getStart()));
        assertThat(booking.getEnd(), equalTo(createBookingRequest.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        assertThat(booking.getBooker().getId(), equalTo(bookerId));
        assertThat(booking.getBooker().getName(), equalTo(createUserRequest2.getName()));
        assertThat(booking.getBooker().getEmail(), equalTo(createUserRequest2.getEmail()));
        assertThat(booking.getItem().getId(), equalTo(1L));
        assertThat(booking.getItem().getName(), equalTo(createItemRequest.getName()));
        assertThat(booking.getItem().getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(booking.getItem().getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerId));
        assertThat(booking.getItem().getOwner().getName(), equalTo(createUserRequest.getName()));
        assertThat(booking.getItem().getOwner().getEmail(), equalTo(createUserRequest.getEmail()));
        assertThat(booking.getItem().getRequest(), equalTo(null));
    }

    @Test
    void update() {
        Long ownerId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(bookingId, ownerId, true);

        TypedQuery<Booking> query = em.createQuery("select b from Booking as b where b.booker.id=:id", Booking.class);
        Booking booking = query.setParameter("id", bookerId).getSingleResult();

        assertThat(booking.getId(), equalTo(bookingId));
        assertThat(booking.getStart(), equalTo(createBookingRequest.getStart()));
        assertThat(booking.getEnd(), equalTo(createBookingRequest.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
        assertThat(booking.getBooker().getId(), equalTo(bookerId));
        assertThat(booking.getBooker().getName(), equalTo(createUserRequest2.getName()));
        assertThat(booking.getBooker().getEmail(), equalTo(createUserRequest2.getEmail()));
        assertThat(booking.getItem().getId(), equalTo(1L));
        assertThat(booking.getItem().getName(), equalTo(createItemRequest.getName()));
        assertThat(booking.getItem().getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(booking.getItem().getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerId));
        assertThat(booking.getItem().getOwner().getName(), equalTo(createUserRequest.getName()));
        assertThat(booking.getItem().getOwner().getEmail(), equalTo(createUserRequest.getEmail()));
        assertThat(booking.getItem().getRequest(), equalTo(null));
    }

    @Test
    void findByIdAndOwnerOrBookerId() {
        Long ownerId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);

        BookingResponse bookingResponse = bookingService.findByIdAndUserId(bookingId, ownerId);

        assertThat(bookingResponse.getId(), equalTo(bookingId));
        assertThat(bookingResponse.getStart(), equalTo(createBookingRequest.getStart()));
        assertThat(bookingResponse.getEnd(), equalTo(createBookingRequest.getEnd()));
        assertThat(bookingResponse.getStatus(), equalTo(Status.WAITING));
        assertThat(bookingResponse.getBooker().getId(), equalTo(bookerId));
        assertThat(bookingResponse.getItem().getId(), equalTo(1L));
    }

    @Test
    void findByBookerIdAndState() {
        long ownerId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);

        List<BookingResponse> bookingResponses =
                bookingService.findByBookerIdAndState(bookerId, "ALL", 0L, 20);

        assertThat(bookingResponses.size(), equalTo(1));
        assertThat(bookingResponses.get(0).getId(), equalTo(bookingId));
        assertThat(bookingResponses.get(0).getStart(), equalTo(createBookingRequest.getStart()));
        assertThat(bookingResponses.get(0).getEnd(), equalTo(createBookingRequest.getEnd()));
        assertThat(bookingResponses.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingResponses.get(0).getBooker().getId(), equalTo(bookerId));
        assertThat(bookingResponses.get(0).getItem().getId(), equalTo(1L));
    }

    @Test
    void findByItemOwnerIdAndState() {
        Long ownerId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);

        List<BookingResponse> bookingResponses =
                bookingService.findByItemOwnerIdAndState(ownerId, "WAITING", 0L, 20);

        assertThat(bookingResponses.size(), equalTo(1));
        assertThat(bookingResponses.get(0).getId(), equalTo(bookingId));
        assertThat(bookingResponses.get(0).getStart(), equalTo(createBookingRequest.getStart()));
        assertThat(bookingResponses.get(0).getEnd(), equalTo(createBookingRequest.getEnd()));
        assertThat(bookingResponses.get(0).getStatus(), equalTo(Status.WAITING));
        assertThat(bookingResponses.get(0).getBooker().getId(), equalTo(bookerId));
        assertThat(bookingResponses.get(0).getItem().getId(), equalTo(1L));
    }

    @Test
    void findById() {
        long ownerId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);

        Booking booking = bookingService.findById(bookingId);

        assertThat(booking.getId(), equalTo(bookingId));
        assertThat(booking.getStart(), equalTo(createBookingRequest.getStart()));
        assertThat(booking.getEnd(), equalTo(createBookingRequest.getEnd()));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        assertThat(booking.getBooker().getId(), equalTo(bookerId));
        assertThat(booking.getBooker().getName(), equalTo(createUserRequest2.getName()));
        assertThat(booking.getBooker().getEmail(), equalTo(createUserRequest2.getEmail()));
        assertThat(booking.getItem().getId(), equalTo(1L));
        assertThat(booking.getItem().getName(), equalTo(createItemRequest.getName()));
        assertThat(booking.getItem().getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(booking.getItem().getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(booking.getItem().getOwner().getId(), equalTo(ownerId));
        assertThat(booking.getItem().getOwner().getName(), equalTo(createUserRequest.getName()));
        assertThat(booking.getItem().getOwner().getEmail(), equalTo(createUserRequest.getEmail()));
        assertThat(booking.getItem().getRequest(), equalTo(null));
    }
}