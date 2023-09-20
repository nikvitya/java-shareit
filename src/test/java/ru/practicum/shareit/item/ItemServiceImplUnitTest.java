package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.Status;
import ru.practicum.shareit.exception.IncompatibleUserIdException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.request.entity.Request;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {

    private static ItemService itemService;
    private static ItemService itemServiceSpy;
    private static User user;
    private static User user2;
    private static User user3;
    private static Booking booking;
    private static Comment comment;
    private static Request request;
    private static String itemName;
    private static String itemDescription;
    private static Item item;
    private static Item item2;
    private static CreateItemRequest createItemRequest;
    private static CreateItemRequest createItem2Request;
    private static UpdateItemRequest updateItemRequest;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserService userService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    RequestService requestService;

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository,
                bookingRepository,
                userService,
                commentRepository,
                requestService);
        itemServiceSpy = spy(itemService);
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

        booking = new Booking()
                .setId(1L)
                .setStart(LocalDateTime.now().minusDays(2))
                .setEnd(LocalDateTime.now().minusDays(1))
                .setStatus(Status.APPROVED)
                .setBooker(user2)
                .setItem(item);

        comment = new Comment()
                .setId(1L)
                .setText("Мощная дрель!")
                .setAuthor(user2)
                .setItem(item);

        request = new Request()
                .setId(1L)
                .setDescription("Дрель ударная 20V")
                .setRequestor(user3);

        itemName = "Дрель";
        itemDescription = "Ударная 20V";

        item = new Item()
                .setId(1L)
                .setName(itemName)
                .setDescription(itemDescription)
                .setAvailable(true)
                .setBookings(List.of(booking))
                .setComments(List.of(comment))
                .setOwner(user)
                .setRequest(request);

        item2 = new Item()
                .setId(2L)
                .setName("Ноутбук")
                .setDescription("Lenovo Legion 5")
                .setAvailable(false)
                .setOwner(user2);

        createItemRequest = new CreateItemRequest(
                itemName,
                itemDescription,
                item.getAvailable(),
                item.getRequest().getId());

        createItem2Request = new CreateItemRequest(
                item2.getName(),
                item2.getDescription(),
                item2.getAvailable(),
                null);

        updateItemRequest = new UpdateItemRequest(
                itemName,
                itemDescription,
                item.getAvailable());
    }

    @Test
    void givenCorrectOwnerIdAndItemDto_whenSaveItem_thenReturnAnotherItemDto() {
        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(requestService.findById(anyLong()))
                .thenReturn(request);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse itemResponse = itemService.saveItem(item.getOwner().getId(), createItemRequest);
        assertThat(itemResponse.getId(), equalTo(item.getId()));
        assertThat(itemResponse.getName(), equalTo(item.getName()));
        assertThat(itemResponse.getDescription(), equalTo(item.getDescription()));
        assertThat(itemResponse.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemResponse.getRequestId(), equalTo(item.getRequest().getId()));
        verify(userService, times(1)).findById(anyLong());
        verify(requestService, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenNonExistentOwnerId_whenSaveItem_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        lenient().when(requestService.findById(anyLong()))
                .thenReturn(request);
        lenient().when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertThrows(NotFoundException.class, () -> itemService.saveItem(99L, createItemRequest));
        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenNonExistentRequestId_whenSaveItem_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenReturn(user);
        when(requestService.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        lenient().when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertThrows(NotFoundException.class, () -> itemService.saveItem(item.getOwner().getId(), new CreateItemRequest(
                itemName, itemDescription, item.getAvailable(), 99L)));
        verify(userService, times(1)).findById(anyLong());
        verify(requestService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenWithoutRequestId_whenSaveItem_thenReturnAnotherItemDto() {
        when(userService.findById(anyLong()))
                .thenReturn(user2);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item2);

        ItemResponse itemResponse = itemService.saveItem(item2.getOwner().getId(),
                createItem2Request);
        assertThat(itemResponse.getId(), equalTo(item2.getId()));
        assertThat(itemResponse.getName(), equalTo(item2.getName()));
        assertThat(itemResponse.getDescription(), equalTo(item2.getDescription()));
        assertThat(itemResponse.getAvailable(), equalTo(item2.getAvailable()));
        assertThat(itemResponse.getRequestId(), equalTo(null));
        verify(userService, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenCorrectOwnerIdAndItemIdAndItemDto_whenUpdateNameOrDescriptionOrAvailable_thenReturnAnotherItemDto() {
        when(userService.findById(anyLong()))
                .thenReturn(user);
        doReturn(item).when(itemServiceSpy).findById(anyLong());
        when(itemRepository.save(any(Item.class)))
                .thenReturn(new Item()
                        .setId(item.getId())
                        .setName("Перфоратор")
                        .setDescription("С долотом 18 мм")
                        .setAvailable(false)
                        .setBookings(item.getBookings())
                        .setComments(item.getComments())
                        .setOwner(item.getOwner())
                        .setRequest(item.getRequest()));

        ItemResponse itemResponse = itemServiceSpy.update(item.getOwner().getId(), item.getId(), new UpdateItemRequest(
                "Перфоратор",
                "С долотом 18 мм",
                false));
        assertThat(itemResponse.getId(), equalTo(item.getId()));
        assertThat(itemResponse.getName(), equalTo("Перфоратор"));
        assertThat(itemResponse.getDescription(), equalTo("С долотом 18 мм"));
        assertThat(itemResponse.getAvailable(), equalTo(false));
        assertThat(itemResponse.getRequestId(), equalTo(item.getRequest().getId()));
        verify(userService, times(1)).findById(anyLong());
        verify(itemServiceSpy, times(1)).update(anyLong(), anyLong(), any(UpdateItemRequest.class));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenEmptyItemDto_whenUpdateNameOrDescriptionOrAvailable_thenReturnAnotherItemDto() {
        when(userService.findById(anyLong()))
                .thenReturn(user);
        doReturn(item).when(itemServiceSpy).findById(anyLong());
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponse itemResponse = itemServiceSpy.update(item.getOwner().getId(), item.getId(),
                new UpdateItemRequest(null, null, null));
        assertThat(itemResponse.getId(), equalTo(item.getId()));
        assertThat(itemResponse.getName(), equalTo(item.getName()));
        assertThat(itemResponse.getDescription(), equalTo(item.getDescription()));
        assertThat(itemResponse.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemResponse.getRequestId(), equalTo(item.getRequest().getId()));
        verify(userService, times(1)).findById(anyLong());
        verify(itemServiceSpy, times(1)).update(anyLong(), anyLong(), any(UpdateItemRequest.class));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenNonExistentOwnerId_whenUpdateNameOrDescriptionOrAvailable_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);
        lenient().doReturn(item).when(itemServiceSpy).findById(anyLong());
        lenient().when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        assertThrows(NotFoundException.class, () -> itemServiceSpy.update(99L, item.getId(), updateItemRequest));
        verify(userService, times(1)).findById(anyLong());
        verify(itemServiceSpy, times(1)).update(anyLong(), anyLong(), any(UpdateItemRequest.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenNonExistentItemId_whenUpdateNameOrDescriptionOrAvailable_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenReturn(user);
        doThrow(NotFoundException.class).when(itemServiceSpy).findById(anyLong());
        lenient().when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertThrows(NotFoundException.class, () -> itemServiceSpy.update(item.getOwner().getId(), 99L,
                updateItemRequest));
        verify(userService, times(1)).findById(anyLong());
        verify(itemServiceSpy, times(1)).update(anyLong(), anyLong(), any(UpdateItemRequest.class));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenOwnerIdNotEqualItemOwnerId_whenUpdateNameOrDescriptionOrAvailable_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenReturn(user3);
        doReturn(item).when(itemServiceSpy).findById(anyLong());
        lenient().when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertThrows(IncompatibleUserIdException.class, () -> itemServiceSpy.update(user3.getId(), item.getId(), updateItemRequest));
        verify(userService, times(1)).findById(anyLong());
        verify(itemServiceSpy, times(1)).update(anyLong(), anyLong(), any(UpdateItemRequest.class));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenCorrectItemId_whenFindById_thenReturnItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Item returnedItem = itemService.findById(item.getId());
        assertThat(returnedItem.getId(), equalTo(item.getId()));
        assertThat(returnedItem.getName(), equalTo(item.getName()));
        assertThat(returnedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(returnedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(returnedItem.getBookings(), equalTo(item.getBookings()));
        assertThat(returnedItem.getComments(), equalTo(item.getComments()));
        assertThat(returnedItem.getOwner(), equalTo(item.getOwner()));
        assertThat(returnedItem.getRequest(), equalTo(item.getRequest()));
        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenNonExistentItemId_whenFindById_thenThrowException() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.findById(99L));
        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenCorrectItemIdAndOwnerId_whenFindDtoById_thenReturnItemDto() {
        Page<Booking> lastBooking = new PageImpl<>(List.of(booking));
        Page<Booking> nextBooking = new PageImpl<>(Collections.emptyList());

        doReturn(item).when(itemServiceSpy).findById(anyLong());
        when(commentRepository.findByItem_IdOrderByCreatedAsc(anyLong()))
                .thenReturn(List.of(CommentMapper.toCommentResponse(comment)));
        when(bookingRepository.findLastBooking(anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findNextBooking(anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(nextBooking);

        GetItemResponse getItemResponse = itemServiceSpy.findDtoById(item.getId(), item.getOwner().getId());
        assertThat(getItemResponse.getId(), equalTo(item.getId()));
        assertThat(getItemResponse.getName(), equalTo(item.getName()));
        assertThat(getItemResponse.getDescription(), equalTo(item.getDescription()));
        assertThat(getItemResponse.getAvailable(), equalTo(item.getAvailable()));
        assertThat(getItemResponse.getLastBooking().getId(), equalTo(lastBooking.getContent().get(0).getId()));
        assertThat(getItemResponse.getLastBooking().getBookerId(), equalTo(lastBooking.getContent().get(0).getBooker().getId()));
        assertThat(getItemResponse.getNextBooking(), equalTo(null));
        assertThat(getItemResponse.getComments().get(0).getId(), equalTo(item.getComments().get(0).getId()));
        assertThat(getItemResponse.getComments().get(0).getText(), equalTo(item.getComments().get(0).getText()));
        assertThat(getItemResponse.getComments().get(0).getAuthorName(),
                equalTo(item.getComments().get(0).getAuthor().getName()));
        assertThat(getItemResponse.getComments().get(0).getCreated(), equalTo(item.getComments().get(0).getCreated()));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findByItem_IdOrderByCreatedAsc(anyLong());
        verify(bookingRepository, times(1)).findLastBooking(anyLong(), any(Status.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findNextBooking(anyLong(), any(Status.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(itemServiceSpy, times(1)).findDtoById(anyLong(), anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenWithoutCommentsAndBookings_whenFindDtoById_thenReturnItemDto() {
        Page<Booking> lastBooking = new PageImpl<>(Collections.emptyList());
        Page<Booking> nextBooking = new PageImpl<>(Collections.emptyList());

        doReturn(item2).when(itemServiceSpy).findById(anyLong());
        when(commentRepository.findByItem_IdOrderByCreatedAsc(anyLong()))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBooking(anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findNextBooking(anyLong(), any(Status.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(nextBooking);

        GetItemResponse getItemResponse = itemServiceSpy.findDtoById(item2.getId(), item2.getOwner().getId());
        assertThat(getItemResponse.getId(), equalTo(item2.getId()));
        assertThat(getItemResponse.getName(), equalTo(item2.getName()));
        assertThat(getItemResponse.getDescription(), equalTo(item2.getDescription()));
        assertThat(getItemResponse.getAvailable(), equalTo(item2.getAvailable()));
        assertThat(getItemResponse.getLastBooking(), equalTo(null));
        assertThat(getItemResponse.getNextBooking(), equalTo(null));
        assertThat(getItemResponse.getComments(), equalTo(Collections.emptyList()));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findByItem_IdOrderByCreatedAsc(anyLong());
        verify(bookingRepository, times(1)).findLastBooking(anyLong(), any(Status.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, times(1)).findNextBooking(anyLong(), any(Status.class),
                any(LocalDateTime.class), any(Pageable.class));
        verify(itemServiceSpy, times(1)).findDtoById(anyLong(), anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenOwnerIdNotEqualItemOwnerId_whenFindDtoById_thenReturnAnotherItemDto() {
        doReturn(item).when(itemServiceSpy).findById(anyLong());
        when(commentRepository.findByItem_IdOrderByCreatedAsc(anyLong()))
                .thenReturn(Collections.emptyList());

        GetItemResponse getItemResponse = itemServiceSpy.findDtoById(item.getId(), user3.getId());
        assertThat(getItemResponse.getId(), equalTo(item.getId()));
        assertThat(getItemResponse.getName(), equalTo(item.getName()));
        assertThat(getItemResponse.getDescription(), equalTo(item.getDescription()));
        assertThat(getItemResponse.getAvailable(), equalTo(item.getAvailable()));
        assertThat(getItemResponse.getLastBooking(), equalTo(null));
        assertThat(getItemResponse.getNextBooking(), equalTo(null));
        assertThat(getItemResponse.getComments(), equalTo(Collections.emptyList()));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findByItem_IdOrderByCreatedAsc(anyLong());
        verify(itemServiceSpy, times(1)).findDtoById(anyLong(), anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenNonExistentItemId_whenFindDtoById_thenThrowException() {
        doThrow(NotFoundException.class).when(itemServiceSpy).findById(anyLong());
        lenient().when(commentRepository.findByItem_IdOrderByCreatedAsc(anyLong()))
                .thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> itemServiceSpy.findDtoById(item.getId(), user3.getId()));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verify(itemServiceSpy, times(1)).findDtoById(anyLong(), anyLong());
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenOwnerIdAndFromAndSize_whenFindByOwnerId_thenReturnListOfItemDto() {
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<GetItemResponse> getItemResponses = itemService.findByOwnerId(item.getOwner().getId(), 0L, 20);
        assertThat(getItemResponses.get(0).getId(), equalTo(item.getId()));
        assertThat(getItemResponses.get(0).getName(), equalTo(item.getName()));
        assertThat(getItemResponses.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(getItemResponses.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(getItemResponses.get(0).getLastBooking().getId(), equalTo(booking.getId()));
        assertThat(getItemResponses.get(0).getLastBooking().getBookerId(), equalTo(booking.getBooker().getId()));
        assertThat(getItemResponses.get(0).getNextBooking(), equalTo(null));
        assertThat(getItemResponses.get(0).getComments().get(0).getId(), equalTo(item.getComments().get(0).getId()));
        assertThat(getItemResponses.get(0).getComments().get(0).getText(), equalTo(item.getComments().get(0).getText()));
        assertThat(getItemResponses.get(0).getComments().get(0).getAuthorName(),
                equalTo(item.getComments().get(0).getAuthor().getName()));
        assertThat(getItemResponses.get(0).getComments().get(0).getCreated(), equalTo(item.getComments().get(0).getCreated()));
        verify(itemRepository, times(1)).findByOwnerId(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenCorrectTextAndFromAndSize_whenSearchAvailableItemByText_thenReturnListOfItemDto() {
        when(itemRepository.searchAvailableItemsByText(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(ItemMapper.toItemResponse(item))));

        List<ItemResponse> itemResponses = itemService.searchAvailableItemsByText(item.getName(), 0L, 20);
        List<ItemResponse> itemResponses2 = itemService.searchAvailableItemsByText(item.getDescription(), 0L, 20);
        assertThat(itemResponses.size(), equalTo(1));
        assertThat(itemResponses.get(0).getId(), equalTo(item.getId()));
        assertThat(itemResponses.get(0).getName(), equalTo(item.getName()));
        assertThat(itemResponses.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(itemResponses.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemResponses.get(0).getRequestId(), equalTo(item.getRequest().getId()));
        assertThat(itemResponses2.size(), equalTo(1));
        assertThat(itemResponses2.get(0).getId(), equalTo(item.getId()));
        assertThat(itemResponses2.get(0).getName(), equalTo(item.getName()));
        assertThat(itemResponses2.get(0).getDescription(), equalTo(item.getDescription()));
        assertThat(itemResponses2.get(0).getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemResponses2.get(0).getRequestId(), equalTo(item.getRequest().getId()));
        verify(itemRepository, times(2)).searchAvailableItemsByText(anyString(), any(Pageable.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenBlankText_whenSearchAvailableItemByText_thenReturnEmptyList() {
        List<ItemResponse> itemResponses = itemService.searchAvailableItemsByText(" ", 0L, 20);
        assertThat(itemResponses.size(), equalTo(0));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }

    @Test
    void givenCorrectAuthorIdAndItemIdAndCommentDto_whenSaveComment_thenReturnCommentDto() {
        Comment newComment = new Comment()
                .setId(2L)
                .setText("Супер!")
                .setItem(item)
                .setAuthor(user2);

        when(bookingRepository.findFirstByBooker_IdAndItem_IdAndStatusAndEndIsBefore(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class)))
                .thenReturn(booking);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(newComment);
        doReturn(item).when(itemServiceSpy).findById(anyLong());
        when(userService.findById(anyLong()))
                .thenReturn(user2);

        CommentResponse commentResponse = itemServiceSpy.saveComment(user2.getId(), item.getId(),
                new CreateCommentRequest().setText("Супер!"));
        assertThat(commentResponse.getId(), equalTo(2L));
        assertThat(commentResponse.getText(), equalTo("Супер!"));
        assertThat(commentResponse.getAuthorName(), equalTo(user2.getName()));
        verify(bookingRepository, times(1)).findFirstByBooker_IdAndItem_IdAndStatusAndEndIsBefore(
                anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(itemServiceSpy, times(1)).findById(anyLong());
        verify(userService, times(1)).findById(anyLong());
        verify(itemServiceSpy, times(1)).saveComment(anyLong(), anyLong(),
                any(CreateCommentRequest.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository,
                itemServiceSpy);
    }

    @Test
    void givenNoBookingByThisUserBeforeComment_whenSaveComment_thenThrowException() {
        when(bookingRepository.findFirstByBooker_IdAndItem_IdAndStatusAndEndIsBefore(anyLong(), anyLong(),
                any(Status.class), any(LocalDateTime.class)))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.saveComment(user3.getId(), item.getId(),
                new CreateCommentRequest().setText("Супер!")));
        verify(bookingRepository, times(1)).findFirstByBooker_IdAndItem_IdAndStatusAndEndIsBefore(
                anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class));
        verifyNoMoreInteractions(userService, requestService, itemRepository, bookingRepository, commentRepository);
    }
}