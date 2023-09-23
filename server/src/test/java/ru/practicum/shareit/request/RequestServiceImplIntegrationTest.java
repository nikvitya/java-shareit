package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.CreateRequestRequest;
import ru.practicum.shareit.request.dto.GetRequestResponse;
import ru.practicum.shareit.request.entity.Request;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.CreateUserRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
class RequestServiceImplIntegrationTest {
    private final EntityManager em;
    private final RequestService requestService;
    private final UserService userService;

    private static CreateUserRequest createUserRequest;
    private static CreateUserRequest createUserRequest2;
    private static CreateRequestRequest createRequestRequest;

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
    }

    @Test
    void save() {
        Long requestorId = 1L;
        Long requestId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);

        TypedQuery<Request> query = em.createQuery("select r from Request as r where r.id=:id", Request.class);
        Request request = query.setParameter("id", requestId).getSingleResult();

        assertThat(request.getId(), equalTo(requestId));
        assertThat(request.getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(request.getRequestor().getId(), equalTo(requestorId));
        assertThat(request.getCreated(), notNullValue());
    }

    @Test
    void findById() {
        Long requestorId = 1L;
        Long requestId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);

        Request request = requestService.findById(requestId);

        assertThat(request.getId(), equalTo(requestId));
        assertThat(request.getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(request.getRequestor().getId(), equalTo(requestorId));
        assertThat(request.getCreated(), notNullValue());
    }

    @Test
    void findByRequestorId() {
        Long requestorId = 1L;
        Long requestId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);

        List<GetRequestResponse> getRequestResponses = requestService.findByRequestorId(requestorId);

        assertThat(getRequestResponses.get(0).getId(), equalTo(requestId));
        assertThat(getRequestResponses.get(0).getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(getRequestResponses.get(0).getCreated(), notNullValue());
    }

    @Test
    void findRequestsForAnotherRequestors() {
        Long requestorId = 1L;
        Long requestId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);

        List<GetRequestResponse> getRequestResponses = requestService
                .findRequestsForAnotherRequestors(2L, 0L, 20);

        assertThat(getRequestResponses.get(0).getId(), equalTo(requestId));
        assertThat(getRequestResponses.get(0).getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(getRequestResponses.get(0).getCreated(), notNullValue());
    }

    @Test
    void findDtoById() {
        Long requestorId = 1L;
        Long requestId = 1L;

        userService.save(createUserRequest);
        userService.save(createUserRequest2);
        requestService.save(createRequestRequest, requestorId);

        GetRequestResponse getRequestResponse = requestService.findDtoById(requestId, 2L);

        assertThat(getRequestResponse.getId(), equalTo(requestId));
        assertThat(getRequestResponse.getDescription(), equalTo(createRequestRequest.getDescription()));
        assertThat(getRequestResponse.getCreated(), notNullValue());
    }
}