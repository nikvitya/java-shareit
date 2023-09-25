package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.dto.CreateRequestRequest;
import ru.practicum.shareit.request.dto.CreateRequestResponse;
import ru.practicum.shareit.request.dto.GetRequestResponse;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.entity.User;

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
class RequestServiceImplUnitTest {
    private static RequestService requestService;
    private static RequestService requestServiceSpy;
    private static User user;
    private static User user3;
    private static Request request;
    private static CreateRequestRequest createRequestRequest;
    private static Item item;

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserService userService;

    @BeforeEach
    void beforeEach() {
        requestService = new RequestServiceImpl(requestRepository, userService);
        requestServiceSpy = spy(requestService);
    }

    @BeforeAll
    static void beforeAll() {
        user = new User()
                .setId(1L)
                .setName("Игорь")
                .setEmail("igor@mail.ru");

        user3 = new User()
                .setId(3L)
                .setName("Павел")
                .setEmail("pavel@mail.ru");

        item = new Item()
                .setId(1L)
                .setName("Дрель")
                .setDescription("Ударная 20V")
                .setAvailable(true)
                .setOwner(user)
                .setRequest(request);

        request = new Request()
                .setId(1L)
                .setDescription("Дрель ударная 20V")
                .setRequestor(user3)
                .setItems(List.of(item));

        createRequestRequest = new CreateRequestRequest()
                .setDescription(request.getDescription());
    }

    @Test
    void givenCorrectRequestorId_whenSave_thenReturnRequestDto() {
        when(userService.findById(anyLong()))
                .thenReturn(request.getRequestor());
        when(requestRepository.save(any(Request.class)))
                .thenReturn(request);

        CreateRequestResponse createRequestResponse =
                requestService.save(createRequestRequest, request.getRequestor().getId());
        assertThat(createRequestResponse.getId(), equalTo(request.getId()));
        assertThat(createRequestResponse.getDescription(), equalTo(request.getDescription()));
        assertThat(createRequestResponse.getCreated(), equalTo(request.getCreated()));
        verify(userService, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any(Request.class));
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenNonExistentRequestorId_whenSave_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> requestService.save(createRequestRequest, 99L));
        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenCorrectRequestId_whenFindById_thenReturnRequest() {
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        Request returnedRequest = requestService.findById(request.getId());
        assertThat(returnedRequest.getId(), equalTo(request.getId()));
        assertThat(returnedRequest.getDescription(), equalTo(request.getDescription()));
        assertThat(returnedRequest.getRequestor(), equalTo(request.getRequestor()));
        assertThat(returnedRequest.getItems(), equalTo(request.getItems()));
        assertThat(returnedRequest.getCreated(), equalTo(request.getCreated()));
        verify(requestRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenNonExistentRequestId_whenFindById_thenThrowException() {
        when(requestRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> requestService.findById(99L));
        verify(requestRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenCorrectRequestorId_whenFindByRequestorId_thenReturnListOfRequestDto() {
        when(userService.findById(anyLong()))
                .thenReturn(user3);
        when(requestRepository.findByRequestorId(user3.getId()))
                .thenReturn(List.of(request));

        List<GetRequestResponse> getRequestResponses = requestService.findByRequestorId(user3.getId());
        assertThat(getRequestResponses.get(0).getId(), equalTo(request.getId()));
        assertThat(getRequestResponses.get(0).getDescription(), equalTo(request.getDescription()));
        assertThat(getRequestResponses.get(0).getCreated(), equalTo(request.getCreated()));
        assertThat(getRequestResponses.get(0).getItems().get(0).getId(), equalTo(request.getItems().get(0).getId()));
        assertThat(getRequestResponses.get(0).getItems().get(0).getName(), equalTo(request.getItems().get(0).getName()));
        assertThat(getRequestResponses.get(0).getItems().get(0).getDescription(),
                equalTo(request.getItems().get(0).getDescription()));
        assertThat(getRequestResponses.get(0).getItems().get(0).getAvailable(),
                equalTo(request.getItems().get(0).getAvailable()));
        verify(userService, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findByRequestorId(anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenNonExistentRequestorId_whenFindByRequestorId_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> requestService.findByRequestorId(99L));
        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenRequestorId_whenFindRequestsForAnotherRequestors_thenReturnListOfRequestDto() {
        when(requestRepository.findByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<GetRequestResponse> getRequestResponses = requestService.findRequestsForAnotherRequestors(user3.getId(),
                0L, 20);
        assertThat(getRequestResponses.size(), equalTo(0));
        verify(requestRepository, times(1)).findByRequestorIdNot(anyLong(), any(Pageable.class));
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenCorrectRequestIdAndUserId_whenFindDtoById_thenReturnRequestDto() {
        when(userService.findById(anyLong()))
                .thenReturn(user);
        doReturn(request).when(requestServiceSpy).findById(anyLong());

        GetRequestResponse getRequestResponse = requestServiceSpy.findDtoById(request.getId(), user.getId());
        assertThat(getRequestResponse.getId(), equalTo(request.getId()));
        assertThat(getRequestResponse.getDescription(), equalTo(request.getDescription()));
        assertThat(getRequestResponse.getCreated(), equalTo(request.getCreated()));
        assertThat(getRequestResponse.getItems().get(0).getId(), equalTo(request.getItems().get(0).getId()));
        assertThat(getRequestResponse.getItems().get(0).getName(), equalTo(request.getItems().get(0).getName()));
        assertThat(getRequestResponse.getItems().get(0).getDescription(),
                equalTo(request.getItems().get(0).getDescription()));
        assertThat(getRequestResponse.getItems().get(0).getAvailable(),
                equalTo(request.getItems().get(0).getAvailable()));
        verify(userService, times(1)).findById(anyLong());
        verify(requestServiceSpy, times(1)).findById(anyLong());
        verify(requestServiceSpy, times(1)).findDtoById(anyLong(), anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenNonExistentUserId_whenFindDtoById_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> requestServiceSpy.findDtoById(request.getId(), 99L));
        verify(userService, times(1)).findById(anyLong());
        verify(requestServiceSpy, times(1)).findDtoById(anyLong(), anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }

    @Test
    void givenNonExistentRequestId_whenFindDtoById_thenThrowException() {
        when(userService.findById(anyLong()))
                .thenReturn(user);
        doThrow(NotFoundException.class).when(requestServiceSpy).findById(anyLong());

        assertThrows(NotFoundException.class, () -> requestServiceSpy.findDtoById(99L, user.getId()));
        verify(userService, times(1)).findById(anyLong());
        verify(requestServiceSpy, times(1)).findById(anyLong());
        verify(requestServiceSpy, times(1)).findDtoById(anyLong(), anyLong());
        verifyNoMoreInteractions(userService, requestRepository);
    }
}