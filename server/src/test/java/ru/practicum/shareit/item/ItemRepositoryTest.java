package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.util.OffsetBasedPageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    private User owner = new User().setName("Денис").setEmail("denis@gmail.com");
    private User requestor = new User().setName("Павел").setEmail("pavel@gmail.com");
    private Request request = new Request().setDescription("нужна ударная дрель").setRequestor(requestor);
    private Item item = new Item().setName("Дрель")
            .setDescription("ударная")
            .setAvailable(true)
            .setOwner(owner)
            .setRequest(request);
    private Booking booking = new Booking().setStart(LocalDateTime.now())
            .setEnd(LocalDateTime.now().plusNanos(1))
            .setStatus(Status.APPROVED)
            .setBooker(requestor)
            .setItem(item);
    private Comment comment = new Comment().setText("супер!").setAuthor(requestor).setItem(item);

    @BeforeEach
    void beforeEach() {
        em.persist(owner);
        em.persist(requestor);
        em.persist(request);
        em.persist(item);
        em.persist(booking);
        em.persist(comment);
    }

    @Test
    void searchAvailableItemsByText() {
        ItemResponse itemResponse = itemRepository
                .searchAvailableItemsByText("дре", new OffsetBasedPageRequest(0, 1)).toList().get(0);

        assertNotNull(itemResponse);
        assertNotNull(itemResponse.getId());
        assertNotNull(itemResponse.getRequestId());
        assertEquals(itemResponse.getName(), item.getName());
        assertEquals(itemResponse.getDescription(), item.getDescription());
        assertEquals(itemResponse.getAvailable(), item.getAvailable());
    }

    @Test
    void findByOwnerId() {
        Item itemReturned = itemRepository.findByOwnerId(owner.getId(), new OffsetBasedPageRequest(0, 1))
                .toList().get(0);

        assertNotNull(itemReturned);
        assertNotNull(itemReturned.getId());
        assertEquals(itemReturned.getName(), item.getName());
        assertEquals(itemReturned.getDescription(), item.getDescription());
        assertEquals(itemReturned.getAvailable(), item.getAvailable());
        assertNotNull(itemReturned.getOwner());
        assertNotNull(itemReturned.getRequest());
    }
}