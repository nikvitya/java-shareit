package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.mapper.UserMapper;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse save(CreateUserRequest createUserRequest) {
        User user = UserMapper.toUser(createUserRequest);
        return UserMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %s не найден.", userId)));
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse findDtoById(long userId) {
        return UserMapper.toUserResponse(findById(userId));
    }

    @Override
    public UserResponse update(UpdateUserRequest updateUserRequest, long userId) {
        User user = UserMapper.toUser(updateUserRequest).setId(userId);
        User oldUser = findById(userId);
        return UserMapper.toUserResponse(userRepository.save(oldUser
                .setName(user.getName() == null ? oldUser.getName() : user.getName())
                .setEmail(user.getEmail() == null ? oldUser.getEmail() : user.getEmail())));
    }

    @Override
    public void delete(long userId) {
        findById(userId);
        userRepository.deleteById(userId);
    }
}