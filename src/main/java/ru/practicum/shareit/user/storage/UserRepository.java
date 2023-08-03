package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {

    List<User> getAll();

    User getUserById(Integer id);

    User create(User user);

    User update(Integer userId,User user);

    void deleteUserById(Integer id);

    void deleteAll();

    Map<Integer, User> getUsers();
}