package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.dto.CreateRequestRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@Rollback(value = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final RequestService requestService;
    private final BookingRepository bookingRepository;

    private static CreateUserRequest createUserRequest;
    private static CreateUserRequest createUserRequest2;
    private static CreateItemRequest createItemRequest;
    private static CreateBookingRequest createBookingRequest;
    private static CreateRequestRequest createRequestRequest;
    private static CreateCommentRequest createCommentRequest;

    @BeforeEach
    void beforeEach() {
        createUserRequest = new CreateUserRequest()
                .setName("Игорь")
                .setEmail("igor@mail.ru");

        createUserRequest2 = new CreateUserRequest()
                .setName("Павел")
                .setEmail("pavel@mail.ru");

        createRequestRequest = new CreateRequestRequest()
                .setDescription("Дрель ударная");

        createItemRequest = new CreateItemRequest(
                "Дрель",
                "Ударная 20V",
                true,
                1L);

        createBookingRequest = new CreateBookingRequest(1L,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1));

        createCommentRequest = new CreateCommentRequest()
                .setText("Супер!");
    }

    @Test
    void saveItem() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestorId = 2L;
        Long bookerId = requestorId;
        Long authorId = bookerId;


        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(1L, ownerId, true);
        itemService.saveComment(authorId, 1L, createCommentRequest);

        TypedQuery<Item> query = em.createQuery("select i from Item as i where i.id=:id", Item.class);
        Item item = query.setParameter("id", itemId).getSingleResult();

        assertThat(item, notNullValue());
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(createItemRequest.getName()));
        assertThat(item.getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(item.getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(item.getOwner(), notNullValue());
        assertThat(item.getOwner().getId(), equalTo(ownerId));
        assertThat(item.getOwner().getName(), equalTo(createUserRequest.getName()));
        assertThat(item.getOwner().getEmail(), equalTo(createUserRequest.getEmail()));
        assertThat(item.getRequest(), notNullValue());
        assertThat(item.getRequest().getId(), equalTo(1L));
        assertThat(item.getRequest().getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(item.getRequest().getRequestor().getId(), equalTo(requestorId));
    }

    @Test
    void update() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestorId = 2L;
        Long bookerId = requestorId;
        Long authorId = bookerId;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(1L, ownerId, true);
        itemService.saveComment(authorId, 1L, createCommentRequest);
        itemService.update(ownerId, itemId, new UpdateItemRequest(null, "Новая", null));

        TypedQuery<Item> query = em.createQuery("select i from Item as i where i.id=:id", Item.class);
        Item item = query.setParameter("id", itemId).getSingleResult();

        assertThat(item, notNullValue());
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(createItemRequest.getName()));
        assertThat(item.getDescription(), equalTo("Новая"));
        assertThat(item.getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(item.getOwner(), notNullValue());
        assertThat(item.getOwner().getId(), equalTo(ownerId));
        assertThat(item.getOwner().getName(), equalTo(createUserRequest.getName()));
        assertThat(item.getOwner().getEmail(), equalTo(createUserRequest.getEmail()));
        assertThat(item.getRequest(), notNullValue());
        assertThat(item.getRequest().getId(), equalTo(1L));
        assertThat(item.getRequest().getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(item.getRequest().getRequestor().getId(), equalTo(requestorId));
    }

    @Test
    void findById() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestorId = 2L;
        Long bookerId = requestorId;
        Long authorId = bookerId;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(1L, ownerId, true);
        itemService.saveComment(authorId, 1L, createCommentRequest);

        Item item = itemService.findById(itemId);

        assertThat(item, notNullValue());
        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getName(), equalTo(createItemRequest.getName()));
        assertThat(item.getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(item.getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(item.getOwner(), notNullValue());
        assertThat(item.getOwner().getId(), equalTo(ownerId));
        assertThat(item.getOwner().getName(), equalTo(createUserRequest.getName()));
        assertThat(item.getOwner().getEmail(), equalTo(createUserRequest.getEmail()));
        assertThat(item.getRequest(), notNullValue());
        assertThat(item.getRequest().getId(), equalTo(1L));
        assertThat(item.getRequest().getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(item.getRequest().getRequestor().getId(), equalTo(requestorId));
    }

    @Test
    void findDtoById() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestorId = 2L;
        Long bookerId = requestorId;
        Long authorId = bookerId;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(1L, ownerId, true);
        itemService.saveComment(authorId, 1L, createCommentRequest);

        GetItemResponse getItemResponse = itemService.findDtoById(itemId, ownerId);

        assertThat(getItemResponse, notNullValue());
        assertThat(getItemResponse.getId(), equalTo(itemId));
        assertThat(getItemResponse.getName(), equalTo(createItemRequest.getName()));
        assertThat(getItemResponse.getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(getItemResponse.getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(getItemResponse.getLastBooking(), notNullValue());
        assertThat(getItemResponse.getNextBooking(), equalTo(null));
        assertThat(getItemResponse.getComments().size(), equalTo(1));
    }

    @Test
    void findByOwnerId() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestorId = 2L;
        Long bookerId = requestorId;
        Long authorId = bookerId;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(1L, ownerId, true);
        itemService.saveComment(authorId, 1L, createCommentRequest);

        List<GetItemResponse> getItemResponses = itemService.findByOwnerId(ownerId, 0L, 20);

        assertThat(getItemResponses.size(), equalTo(1));
        assertThat(getItemResponses.get(0), notNullValue());
        assertThat(getItemResponses.get(0).getId(), equalTo(itemId));
        assertThat(getItemResponses.get(0).getName(), equalTo(createItemRequest.getName()));
        assertThat(getItemResponses.get(0).getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(getItemResponses.get(0).getAvailable(), equalTo(createItemRequest.getAvailable()));
    }

    @Test
    void searchAvailableItemsByText() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestorId = 2L;
        Long bookerId = requestorId;
        Long authorId = bookerId;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(1L, ownerId, true);
        itemService.saveComment(authorId, 1L, createCommentRequest);

        List<ItemResponse> itemResponses = itemService.searchAvailableItemsByText("ДреЛ", 0L, 20);

        assertThat(itemResponses.size(), equalTo(1));
        assertThat(itemResponses.get(0), notNullValue());
        assertThat(itemResponses.get(0).getId(), equalTo(itemId));
        assertThat(itemResponses.get(0).getName(), equalTo(createItemRequest.getName()));
        assertThat(itemResponses.get(0).getDescription(), equalTo(createItemRequest.getDescription()));
        assertThat(itemResponses.get(0).getAvailable(), equalTo(createItemRequest.getAvailable()));
        assertThat(itemResponses.get(0).getRequestId(), equalTo(createItemRequest.getRequestId()));
    }

    @Test
    void saveComment() {
        Long itemId = 1L;
        Long ownerId = 1L;
        Long requestorId = 2L;
        Long bookerId = requestorId;
        Long authorId = bookerId;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);
        itemService.saveItem(ownerId, createItemRequest);
        bookingService.save(bookerId, createBookingRequest);
        bookingService.update(1L, ownerId, true);
        itemService.saveComment(authorId, 1L, createCommentRequest);

        TypedQuery<Comment> query = em.createQuery("select c from Comment as c where c.id=:id", Comment.class);
        Comment comment = query.setParameter("id", 1L).getSingleResult();

        assertThat(comment.getId(), equalTo(1L));
        assertThat(comment.getText(), equalTo(createCommentRequest.getText()));
        assertThat(comment.getItem(), notNullValue());
        assertThat(comment.getItem().getId(), equalTo(itemId));
        assertThat(comment.getAuthor(), notNullValue());
        assertThat(comment.getAuthor().getId(), equalTo(authorId));
    }
}