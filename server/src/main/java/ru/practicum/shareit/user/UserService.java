package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

public interface UserService {
    UserResponse save(CreateUserRequest createUserRequest);

    List<UserResponse> findAll();

    UserResponse findDtoById(long userId);

    User findById(long userId);

    UserResponse update(UpdateUserRequest updateUserRequest, long userId);

    void delete(long userId);
}