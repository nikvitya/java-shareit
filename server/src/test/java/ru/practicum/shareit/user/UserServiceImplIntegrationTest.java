package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@Rollback(value = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    private static CreateUserRequest createUserRequest;
    private static UpdateUserRequest updateUserRequest;

    @BeforeEach
    void beforeEach() {
        createUserRequest = new CreateUserRequest()
                .setName("Игорь")
                .setEmail("igor@mail.ru");

        updateUserRequest = new UpdateUserRequest()
                .setName("Паша")
                .setEmail("pasha@mail.ru");
    }

    @Test
    void save() {
        Long userId = 1L;
        userService.save(createUserRequest);

        TypedQuery<User> query = em.createQuery("select u from User as u where u.id=:id", User.class);
        User user = query.setParameter("id", userId).getSingleResult();

        assertThat(user.getId(), equalTo(userId));
        assertThat(user.getName(), equalTo(createUserRequest.getName()));
        assertThat(user.getEmail(), equalTo(createUserRequest.getEmail()));
    }

    @Test
    void findAll() {
        Long userId = 1L;
        userService.save(createUserRequest);

        List<UserResponse> userResponses = userService.findAll();

        assertThat(userResponses.size(), equalTo(1));
        assertThat(userResponses.get(0).getId(), equalTo(userId));
        assertThat(userResponses.get(0).getName(), equalTo(createUserRequest.getName()));
        assertThat(userResponses.get(0).getEmail(), equalTo(createUserRequest.getEmail()));
    }

    @Test
    void findDtoById() {
        Long userId = 1L;
        userService.save(createUserRequest);

        UserResponse userResponse = userService.findDtoById(userId);

        assertThat(userResponse.getId(), equalTo(userId));
        assertThat(userResponse.getName(), equalTo(createUserRequest.getName()));
        assertThat(userResponse.getEmail(), equalTo(createUserRequest.getEmail()));
    }

    @Test
    void findById() {
        Long userId = 1L;
        userService.save(createUserRequest);

        User user = userService.findById(userId);

        assertThat(user.getId(), equalTo(userId));
        assertThat(user.getName(), equalTo(createUserRequest.getName()));
        assertThat(user.getEmail(), equalTo(createUserRequest.getEmail()));
    }

    @Test
    void update() {
        Long userId = 1L;
        userService.save(createUserRequest);
        userService.update(updateUserRequest, userId);

        TypedQuery<User> query = em.createQuery("select u from User as u where u.id=:id", User.class);
        User user = query.setParameter("id", userId).getSingleResult();

        assertThat(user.getId(), equalTo(userId));
        assertThat(user.getName(), equalTo(updateUserRequest.getName()));
        assertThat(user.getEmail(), equalTo(updateUserRequest.getEmail()));
    }

    @Test
    void delete() {
        Long userId = 1L;
        userService.save(createUserRequest);
        userService.delete(userId);

        TypedQuery<User> query = em.createQuery("select u from User as u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(0));
    }
}