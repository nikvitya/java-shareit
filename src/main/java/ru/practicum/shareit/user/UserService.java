package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User add(User user);

    List<User> findAll();

    User findById(long userId);

    User update(User user);

    void delete(long userId);
}