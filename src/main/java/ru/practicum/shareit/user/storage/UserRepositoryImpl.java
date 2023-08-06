package ru.practicum.shareit.user.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Getter
public class UserRepositoryImpl implements UserRepository {

    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    public Map<Integer, User> getUsers() {
        return users;
    }

    private int idGenerator() {
        return id++;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(idGenerator());

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(Integer userId, User user) {

        User updatedUser = User.builder()
                .id(userId)
                .name(user.getName() == null ? users.get(userId).getName() : user.getName())
                .email(user.getEmail() == null ? users.get(userId).getEmail() : user.getEmail())
                .build();
        users.put(userId, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUserById(Integer id) {
        users.remove(id);
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
