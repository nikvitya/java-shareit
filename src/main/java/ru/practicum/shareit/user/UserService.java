package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;


import java.util.List;

public interface UserService {
    UserDto add(UserDto userDto);

    List<UserDto> findAll();

    UserDto findById(long userId);

    UserDto update(UserDto userDto, long userId);

    void delete(long userId);
}