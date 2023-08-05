package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.validator.UserDtoValidate;
import ru.practicum.shareit.user.validator.UserValidate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.Constants.USER_EMAIL_ALREADY_EXIST;
import static ru.practicum.shareit.exception.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userStorage;

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(user -> UserMapper.toUserDto(user)).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Integer id) {
        UserValidate.validateId(id);
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        UserDtoValidate.validateUserDto(userDto);

        Set<String> emails = getAllEmails();

        if (emails.contains(userDto.getEmail())) {
            throw new EmailAlreadyExistException(String.format(USER_EMAIL_ALREADY_EXIST, userDto.getEmail()));
        }

        User user = UserMapper.toUser(userDto);

        return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(UserDto userDto, Integer userId) {
        UserDtoValidate.validateDtoId(userId);

        if (!userStorage.getUsers().containsKey(userId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, userId));
        }

        if (userStorage.getUsers().values().stream().anyMatch(u -> u.getEmail().equals(userDto.getEmail()) && !u.getId().equals(userId)))
            throw new EmailAlreadyExistException(String.format(USER_EMAIL_ALREADY_EXIST, userDto.getEmail()));

        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userStorage.update(userId, user));
    }

    @Override
    public void deleteById(Integer id) {
        UserValidate.validateId(id);
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND, id));
        }

        userStorage.deleteUserById(id);
    }

    @Override
    public void deleteAll() {
        userStorage.deleteAll();
    }

    private Set<String> getAllEmails() {
        return userStorage.getAll()
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
    }

}
