package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto getUserById(Integer id);

    UserDto create(UserDto user);

    UserDto update(UserDto userDto, Integer userId);

    void deleteById(Integer id);

    void deleteAll();

}
