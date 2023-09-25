package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    private static UserService userService;
    private static UserService userServiceSpy;
    private static User user;
    private static CreateUserRequest createUserRequest;
    private static UpdateUserRequest updateUserRequest;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
        userServiceSpy = spy(userService);
    }

    @BeforeAll
    static void beforeAll() {
        user = new User()
                .setId(1L)
                .setName("Игорь")
                .setEmail("igor@mail.ru");

        createUserRequest = new CreateUserRequest()
                .setName(user.getName())
                .setEmail(user.getEmail());

        updateUserRequest = new UpdateUserRequest()
                .setName(user.getName())
                .setEmail(user.getEmail());
    }

    @Test
    void givenUserDto_whenSave_thenReturnAnotherUserDto() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse userResponse = userService.save(createUserRequest);
        assertThat(userResponse.getId(), equalTo(user.getId()));
        assertThat(userResponse.getName(), equalTo(user.getName()));
        assertThat(userResponse.getEmail(), equalTo((user.getEmail())));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenUsersInRepository_whenFindAll_thenReturnListOfUserDto() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));

        List<UserResponse> userResponses = userService.findAll();
        assertThat(userResponses.size(), equalTo(1));
        assertThat(userResponses.get(0).getId(), equalTo(user.getId()));
        assertThat(userResponses.get(0).getName(), equalTo(user.getName()));
        assertThat(userResponses.get(0).getEmail(), equalTo((user.getEmail())));
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenCorrectUserId_whenFindDtoById_thenReturnUserDto() {
        doReturn(user).when(userServiceSpy).findById(anyLong());

        UserResponse userResponse = userServiceSpy.findDtoById(user.getId());
        assertThat(userResponse.getId(), equalTo(user.getId()));
        assertThat(userResponse.getName(), equalTo(user.getName()));
        assertThat(userResponse.getEmail(), equalTo((user.getEmail())));
        verify(userServiceSpy, times(1)).findById(anyLong());
        verify(userServiceSpy, times(1)).findDtoById(anyLong());
        verifyNoMoreInteractions(userRepository, userServiceSpy);
    }

    @Test
    void givenNonExistentUserId_whenFindDtoById_thenThrowException() {
        doThrow(NotFoundException.class).when(userServiceSpy).findById(anyLong());

        assertThrows(NotFoundException.class, () -> userServiceSpy.findDtoById(99L));
        verify(userServiceSpy, times(1)).findById(anyLong());
        verify(userServiceSpy, times(1)).findDtoById(anyLong());
        verifyNoMoreInteractions(userRepository, userServiceSpy);
    }

    @Test
    void givenCorrectUserId_whenFindById_thenReturnUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        User returnedUser = userService.findById(user.getId());
        assertThat(returnedUser.getId(), equalTo(user.getId()));
        assertThat(returnedUser.getName(), equalTo(user.getName()));
        assertThat(returnedUser.getEmail(), equalTo((user.getEmail())));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenNonExistentUserId_whenFindById_thenThrowException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class, () -> userService.findById(99L));
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenCorrectUserDtoAndUserId_whenUpdateUserNameOrUserEmail_thenReturnAnotherUserDto() {
        doReturn(user).when(userServiceSpy).findById(anyLong());
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse userResponse = userServiceSpy.update(updateUserRequest, user.getId());
        assertThat(userResponse.getId(), equalTo(user.getId()));
        assertThat(userResponse.getName(), equalTo(user.getName()));
        assertThat(userResponse.getEmail(), equalTo((user.getEmail())));
        verify(userServiceSpy, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userServiceSpy, times(1)).update(any(UpdateUserRequest.class), anyLong());
        verifyNoMoreInteractions(userRepository, userServiceSpy);
    }

    @Test
    void givenNonExistentUserId_whenUpdateUserNameOrUserEmail_thenThrowException() {
        doThrow(NotFoundException.class).when(userServiceSpy).findById(anyLong());

        assertThrows(NotFoundException.class, () -> userServiceSpy.update(updateUserRequest, user.getId()));
        verify(userServiceSpy, times(1)).findById(anyLong());
        verify(userServiceSpy, times(1)).update(any(UpdateUserRequest.class), anyLong());
        verifyNoMoreInteractions(userRepository, userServiceSpy);
    }

    @Test
    void givenCorrectUserId_whenDelete_thenVoid() {
        doReturn(user).when(userServiceSpy).findById(anyLong());

        userServiceSpy.delete(user.getId());
        verify(userServiceSpy, times(1)).findById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
        verify(userServiceSpy, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userRepository, userServiceSpy);
    }

    @Test
    void givenNonExistentUserId_whenDelete_thenThrowException() {
        doThrow(NotFoundException.class).when(userServiceSpy).findById(anyLong());

        assertThrows(NotFoundException.class, () -> userServiceSpy.delete(user.getId()));
        verify(userServiceSpy, times(1)).findById(anyLong());
        verify(userServiceSpy, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userRepository, userServiceSpy);
    }
}