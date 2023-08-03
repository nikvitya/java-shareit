package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        return UserMapper.toUserDto(userService.getUserById(userId));
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.create(userDto));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Integer userId) {
        return UserMapper.toUserDto(userService.update(userDto, userId));
    }


    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
        return "Пользователь c id=" + id + " удален";
    }

    @DeleteMapping
    public String deleteAll() {
        userService.deleteAll();
        return "Все пользователи удалены";
    }

}
