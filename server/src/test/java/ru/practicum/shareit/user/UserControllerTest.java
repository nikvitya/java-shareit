package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    @MockBean
    UserService userService;

    private String name;
    private String email;
    private Long userId;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UserResponse userResponse;

    @BeforeEach
    void beforeEach() {
        name = "Павел";
        email = "pavel@gmail.com";
        userId = 1L;
        createUserRequest = new CreateUserRequest().setName(name).setEmail(email);
        updateUserRequest = new UpdateUserRequest().setName(name).setEmail(email);
        userResponse = new UserResponse(userId, name, email);
    }

    @Test
    void givenCorrectUserDto_whenSave_thenReturnAnotherUserDto() throws Exception {
        when(userService.save(any(CreateUserRequest.class)))
                .thenReturn(userResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));
    }

    @Test
    void givenBlankName_whenSave_thenThrowException() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserRequest.setName(" ")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenEmptyEmail_whenSave_thenThrowException() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserRequest.setEmail("")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidEmail_whenSave_thenThrowException() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(createUserRequest.setEmail("pavel.gmail.com")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNotEmptyUsers_whenFindAll_thenReturnListOfUserDto() throws Exception {
        when(userService.findAll())
                .thenReturn(List.of(userResponse, userResponse));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(userResponse.getName()))
                .andExpect(jsonPath("$[0].email").value(userResponse.getEmail()));
    }

    @Test
    void givenNoUsers_whenFindAll_thenReturnEmptyList() throws Exception {
        when(userService.findAll())
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void givenCorrectUserId_whenFindDtoById_thenReturnUserDto() throws Exception {
        when(userService.findDtoById(anyLong()))
                .thenReturn(userResponse);

        mvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));
    }

    @Test
    void givenNullPathVariable_whenFindDtoById_thenThrowException() throws Exception {
        mvc.perform(get("/users/null")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectUserDtoAndUserId_whenUpdate_thenReturnAnotherUserDto() throws Exception {
        when(userService.update(any(UpdateUserRequest.class), anyLong()))
                .thenReturn(userResponse);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()));
    }

    @Test
    void givenInvalidEmail_whenUpdate_thenThrowException() throws Exception {
        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(updateUserRequest.setEmail("pavel.gmail.com")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNullPathVariable_whenUpdate_thenThrowException() throws Exception {
        mvc.perform(patch("/users/null")
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCorrectUserId_whenDelete_thenReturnNothing() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    void givenNullPathVariable_whenDelete_thenThrowException() throws Exception {
        mvc.perform(delete("/users/null")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(userService);
    }
}