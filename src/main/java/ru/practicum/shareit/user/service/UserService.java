package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User getUserById(Integer id);

    User create(UserDto user);

    User update(UserDto userDto, Integer userId);

    void deleteById(Integer id);

    void deleteAll();

}
