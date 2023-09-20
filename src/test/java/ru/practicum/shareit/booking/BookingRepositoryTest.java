package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    private LocalDateTime now = LocalDateTime.now();
    private User owner = new User()
            .setName("Денис")
            .setEmail("denis@gmail.com");
    private User booker = new User()
            .setName("Павел")
            .setEmail("pavel@gmail.com");
    private Item item = new Item()
            .setName("Дрель")
            .setDescription("ударная")
            .setAvailable(true)
            .setOwner(owner);
    private Booking lastBooking = new Booking()
            .setStart(now.minusDays(1))
            .setEnd(now.minusHours(1))
            .setStatus(Status.APPROVED)
            .setBooker(booker)
            .setItem(item);
    private Booking nextBooking = new Booking()
            .setStart(now.plusHours(1))
            .setEnd(now.plusDays(1))
            .setStatus(Status.APPROVED)
            .setBooker(booker)
            .setItem(item);

    @BeforeEach
    void beforeEach() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(lastBooking);
        em.persist(nextBooking);
    }

    @Test
    void findLastBooking() {

        Booking lastBookingReturned = bookingRepository.findLastBooking(item.getId(), Status.APPROVED, LocalDateTime.now(),
                PageRequest.of(0, 1)).toList().get(0);

        assertNotNull(lastBookingReturned);
        assertEquals(1, lastBookingReturned.getId());
        assertTrue(lastBookingReturned.getStart().isBefore(now));
        assertTrue(lastBookingReturned.getEnd().isBefore(now));
        assertEquals(lastBookingReturned.getStatus(), Status.APPROVED);
        assertNotNull(lastBookingReturned.getBooker());
        assertNotNull(lastBookingReturned.getItem());
    }

    @Test
    void findNextBooking() {
        Booking nextBookingReturned = bookingRepository.findNextBooking(item.getId(), Status.APPROVED, LocalDateTime.now(),
                PageRequest.of(0, 1)).toList().get(0);

        assertNotNull(nextBookingReturned);
        assertEquals(2, nextBookingReturned.getId());
        assertTrue(nextBookingReturned.getStart().isAfter(now));
        assertTrue(nextBookingReturned.getEnd().isAfter(now));
        assertEquals(nextBookingReturned.getStatus(), Status.APPROVED);
        assertNotNull(nextBookingReturned.getBooker());
        assertNotNull(nextBookingReturned.getItem());
    }
}