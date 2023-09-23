package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    ItemService itemService;

    private String name;
    private String description;
    private boolean available;
    private Long requestId;
    private Long itemId;
    private BookingShort lastBooking;
    private BookingShort nextBooking;
    private CreateItemRequest createItemRequest;
    private UpdateItemRequest updateItemRequest;
    private ItemResponse itemResponse;
    private GetItemResponse getItemResponse;

    private CommentResponse commentResponse;
    private CreateCommentRequest createCommentRequest;


    @BeforeEach
    void beforeEach() {
        name = "Дрель";
        description = "Ударная";
        available = true;
        requestId = 1L;
        itemId = 1L;
        lastBooking = new BookingShort() {
            @Override
            public long getId() {
                return 1L;
            }

            @Override
            public long getBookerId() {
                return 1L;
            }
        };
        nextBooking = new BookingShort() {
            @Override
            public long getId() {
                return 2L;
            }

            @Override
            public long getBookerId() {
                return 2L;
            }
        };
        createCommentRequest = new CreateCommentRequest().setText("Дрель супер!");
        commentResponse = new CommentResponse() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getText() {
                return createCommentRequest.getText();
            }

            @Override
            public String getAuthorName() {
                return "Павел";
            }

            @Override
            public LocalDateTime getCreated() {
                return LocalDateTime.now();
            }
        };
        createItemRequest = new CreateItemRequest(name, description, available, requestId);
        updateItemRequest = new UpdateItemRequest(name, description, available);
        itemResponse = new ItemResponse(itemId, name, description, available, requestId);
        getItemResponse = new GetItemResponse(itemId, name, description, available,
                lastBooking, nextBooking, List.of(commentResponse, commentResponse));
    }

    @Test
    void givenCorrectItemDtoAndOwnerId_whenSaveItem_thenReturnAnotherItemDto() throws Exception {
        when(itemService.saveItem(anyLong(), any(CreateItemRequest.class)))
                .thenReturn(itemResponse);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(createItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponse.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemResponse.getRequestId()));
    }

    @Test
    void givenNoUserIdHeader_whenSaveItem_thenThrowException() throws Exception {
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(createItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenBlankItemName_whenSaveItem_thenThrowException() throws Exception {
        createItemRequest.setName("");

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(createItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenBlankItemDescription_whenSaveItem_thenThrowException() throws Exception {
        createItemRequest.setDescription("");

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(createItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNullItemAvailable_whenSaveItem_thenThrowException() throws Exception {
        createItemRequest.setAvailable(null);

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(createItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectItemDtoAndUserIdAndItemId_whenUpdate_thenReturnAnotherItemDto() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(UpdateItemRequest.class)))
                .thenReturn(itemResponse);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponse.getId()))
                .andExpect(jsonPath("$.name").value(itemResponse.getName()))
                .andExpect(jsonPath("$.description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(itemResponse.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemResponse.getRequestId()));
    }

    @Test
    void givenNoUserIdHeader_whenUpdate_thenThrowException() throws Exception {
        mvc.perform(patch("/items/{itemId}", itemId)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNullPathVariable_whenUpdate_thenThrowException() throws Exception {
        mvc.perform(patch("/items/null")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectItemIdAndOwnerId_whenFindById_thenReturnItemDto() throws Exception {
        when(itemService.findDtoById(anyLong(), anyLong()))
                .thenReturn(getItemResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemResponse.getId()))
                .andExpect(jsonPath("$.name").value(getItemResponse.getName()))
                .andExpect(jsonPath("$.description").value(getItemResponse.getDescription()))
                .andExpect(jsonPath("$.available").value(getItemResponse.getAvailable()))
                .andExpect(jsonPath("$.lastBooking").isNotEmpty())
                .andExpect(jsonPath("$.nextBooking").isNotEmpty())
                .andExpect(jsonPath("$.comments").isNotEmpty());
    }

    @Test
    void givenNullPathVariable_whenFindById_thenThrowException() throws Exception {
        mvc.perform(get("/items/null")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoUserIdHeader_whenFindById_thenThrowException() throws Exception {
        mvc.perform(get("/items/{itemId}", itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectOwnerIdWithoutFromAndSize_whenFindByOwnerId_thenReturnListOfItemDto() throws Exception {
        when(itemService.findByOwnerId(anyLong(), anyLong(), anyInt()))
                .thenReturn(List.of(getItemResponse, getItemResponse));

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(getItemResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(getItemResponse.getName()))
                .andExpect(jsonPath("$[0].description").value(getItemResponse.getDescription()))
                .andExpect(jsonPath("$[0].available").value(getItemResponse.getAvailable()))
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].comments").isNotEmpty());
    }

    @Test
    void givenNoUserIdHeader_whenFindByOwnerId_thenThrowException() throws Exception {
        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFromLessThan0_whenFindByOwnerId_thenThrowException() throws Exception {
        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "-1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFromMoreThanMaxOfLong_whenFindByOwnerId_thenThrowException() throws Exception {
        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", String.valueOf(Long.MAX_VALUE + 1))
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenSizeLessThan1_whenFindByOwnerId_thenThrowException() throws Exception {
        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenSizeMoreThan100_whenFindByOwnerId_thenThrowException() throws Exception {
        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "101")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectTextWithoutFromAndSize_whenSearchAvailableItemsByText_thenReturnListOfItemDto() throws Exception {
        when(itemService.searchAvailableItemsByText(anyString(), anyLong(), anyInt()))
                .thenReturn(List.of(itemResponse, itemResponse));

        mvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(itemResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponse.getName()))
                .andExpect(jsonPath("$[0].description").value(itemResponse.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemResponse.getAvailable()));
    }

    @Test
    void givenNoText_whenSearchAvailableItemsByText_thenThrowException() throws Exception {
        mvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFromLessThan0_whenSearchAvailableItemsByText_thenThrowException() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .param("from", "-1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenFromMoreThanMaxOfLong_whenSearchAvailableItemsByText_thenThrowException() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .param("from", String.valueOf(Long.MAX_VALUE + 1))
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenSizeLessThan1_whenSearchAvailableItemsByText_thenThrowException() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenSizeMoreThan100_whenSearchAvailableItemsByText_thenThrowException() throws Exception {
        mvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .param("from", "0")
                        .param("size", "101")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectCommentDtoAndAuthorIdAndItemId_whenSaveComment_thenReturnAnotherCommentDto() throws Exception {
        when(itemService.saveComment(anyLong(), anyLong(), any(CreateCommentRequest.class)))
                .thenReturn(commentResponse);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(createCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponse.getId()))
                .andExpect(jsonPath("$.text").value(commentResponse.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponse.getAuthorName()))
                .andExpect(jsonPath("$.created").value(notNullValue()));
    }

    @Test
    void givenNoUserIdHeader_whenSaveComment_thenThrowException() throws Exception {
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(createCommentRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenBlankText_whenSaveComment_thenThrowException() throws Exception {
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(createCommentRequest.setText("")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}