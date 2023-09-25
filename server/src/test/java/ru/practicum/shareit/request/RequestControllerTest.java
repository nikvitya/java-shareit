package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.CreateRequestRequest;
import ru.practicum.shareit.request.dto.CreateRequestResponse;
import ru.practicum.shareit.request.dto.GetRequestResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.DATE_TIME_FORMATTER;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    RequestService requestService;

    private Long requestorId;
    private Long requestId;
    private String description;
    private LocalDateTime created;
    private ItemResponse itemResponse;
    private CreateRequestRequest createRequestRequest;
    private CreateRequestResponse createRequestResponse;
    private GetRequestResponse getRequestResponse;

    @BeforeEach
    void beforeEach() {
        requestorId = 1L;
        requestId = 1L;
        description = "нужна ударная дрель";
        created = LocalDateTime.now();
        itemResponse = new ItemResponse(1L, "дрель", "ударная", true, requestId);
        createRequestRequest = new CreateRequestRequest().setDescription(description);
        createRequestResponse = new CreateRequestResponse(requestId, description, created);
        getRequestResponse = new GetRequestResponse(requestId, description, created, List.of(itemResponse, itemResponse));
    }

    @Test
    void givenCorrectRequestDtoAndRequestorId_whenSave_thenReturnAnotherRequestDto() throws Exception {
        when(requestService.save(any(CreateRequestRequest.class), anyLong()))
                .thenReturn(createRequestResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createRequestRequest))
                        .header(USER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createRequestResponse.getId()))
                .andExpect(jsonPath("$.description").value(createRequestResponse.getDescription()))
                .andExpect(jsonPath("$.created").value(createRequestResponse.getCreated()
                        .format(DATE_TIME_FORMATTER)));
    }

//    @Test
//    void givenBlankDescription_whenSave_thenReturnAnotherRequestDto() throws Exception {
//        mvc.perform(post("/requests")
//                        .content(mapper.writeValueAsString(createRequestRequest.setDescription("")))
//                        .header(USER_ID_HEADER, requestorId)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void givenNoUserIdHeader_whenSave_thenReturnAnotherRequestDto() throws Exception {
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(createRequestRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectRequestorId_whenFindByRequestorId_thenReturnListOfRequestDto() throws Exception {
        when(requestService.findByRequestorId(anyLong()))
                .thenReturn(List.of(getRequestResponse, getRequestResponse));

        mvc.perform(get("/requests")
                        .header(USER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(getRequestResponse.getId()))
                .andExpect(jsonPath("$[0].description").value(getRequestResponse.getDescription()))
                .andExpect(jsonPath("$[0].created").value(getRequestResponse.getCreated()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].items").isNotEmpty());
    }

    @Test
    void givenNoUserIdHeader_whenFindByRequestorId_thenThrowException() throws Exception {
        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectRequestorIdWithoutFromAndSize_whenFindRequestsForAnotherRequestors_thenReturnListOfRequestDto()
            throws Exception {
        when(requestService.findRequestsForAnotherRequestors(anyLong(), anyLong(), anyInt()))
                .thenReturn(List.of(getRequestResponse, getRequestResponse));

        mvc.perform(get("/requests/all")
                        .header(USER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(getRequestResponse.getId()))
                .andExpect(jsonPath("$[0].description").value(getRequestResponse.getDescription()))
                .andExpect(jsonPath("$[0].created").value(getRequestResponse.getCreated()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$[0].items").isNotEmpty());
    }

    @Test
    void givenNoUserIdHeader_whenFindRequestsForAnotherRequestors_thenThrowException() throws Exception {
        mvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void givenFromLessThan0_whenFindRequestsForAnotherRequestors_thenThrowException() throws Exception {
//        mvc.perform(get("/requests/all")
//                        .header(USER_ID_HEADER, requestorId)
//                        .param("from", "-1")
//                        .param("size", "1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    void givenFromMoreThanMaxOfLong_whenFindRequestsForAnotherRequestors_thenThrowException() throws Exception {
//        mvc.perform(get("/requests/all")
//                        .header(USER_ID_HEADER, requestorId)
//                        .param("from", String.valueOf(Long.MAX_VALUE + 1))
//                        .param("size", "1")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    void givenSizeLessThan1_whenFindRequestsForAnotherRequestors_thenThrowException() throws Exception {
//        mvc.perform(get("/requests/all")
//                        .header(USER_ID_HEADER, requestorId)
//                        .param("from", "0")
//                        .param("size", "0")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

//    @Test
//    void givenSizeMoreThan100_whenFindRequestsForAnotherRequestors_thenThrowException() throws Exception {
//        mvc.perform(get("/requests/all")
//                        .header(USER_ID_HEADER, requestorId)
//                        .param("from", "0")
//                        .param("size", "101")
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void givenCorrectUserIdAndRequestId_whenFindById_thenReturnRequestDto() throws Exception {
        when(requestService.findDtoById(anyLong(), anyLong()))
                .thenReturn(getRequestResponse);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getRequestResponse.getId()))
                .andExpect(jsonPath("$.description").value(getRequestResponse.getDescription()))
                .andExpect(jsonPath("$.created").value(getRequestResponse.getCreated()
                        .format(DATE_TIME_FORMATTER)))
                .andExpect(jsonPath("$.items").isNotEmpty());
    }

    @Test
    void givenNoUserIdHeader_whenFindById_thenThrowException() throws Exception {
        mvc.perform(get("/requests/{requestId}", requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNullPathVariable_whenFindById_thenThrowException() throws Exception {
        mvc.perform(get("/requests/null")
                        .header(USER_ID_HEADER, requestorId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}