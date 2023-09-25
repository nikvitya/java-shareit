package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse save(@RequestBody CreateUserRequest createUserRequest) {
        return userService.save(createUserRequest);
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserResponse findDtoById(@PathVariable long userId) {
        return userService.findDtoById(userId);
    }

    @PatchMapping("/{userId}")
    public UserResponse update(@RequestBody UpdateUserRequest updateUserRequest,
                               @PathVariable long userId) {
        return userService.update(updateUserRequest, userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }
}