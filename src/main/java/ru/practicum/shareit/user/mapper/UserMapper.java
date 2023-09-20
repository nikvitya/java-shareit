package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.entity.User;

@UtilityClass
public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(CreateUserRequest createUserRequest) {
        return new User().setName(createUserRequest.getName()).setEmail(createUserRequest.getEmail());
    }

    public static User toUser(UpdateUserRequest updateUserRequest) {
        return new User().setName(updateUserRequest.getName()).setEmail(updateUserRequest.getEmail());
    }

    public static UserShort toUserDtoShort(User user) {
        return new UserShort().setId(user.getId());
    }
}