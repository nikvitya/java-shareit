package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid CreateUserRequest createUserRequest) {
        return userClient.save(createUserRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findDtoById(@PathVariable long userId) {
        return userClient.findDtoById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody @Valid UpdateUserRequest updateUserRequest,
                                         @PathVariable long userId) {
        return userClient.update(updateUserRequest, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable long userId) {
        return userClient.delete(userId);
    }
}