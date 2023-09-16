package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.CreateGroup;
import ru.practicum.shareit.validation.UpdateGroup;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@RequestBody @Validated(CreateGroup.class) UserDto userDto) {
        return UserMapper.toUserDto(userService.add(UserMapper.toUser(userDto)));
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) {
        return UserMapper.toUserDto(userService.findById(userId));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Validated(UpdateGroup.class) UserDto userDto, @PathVariable long userId) {
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto.setId(userId))));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }
}