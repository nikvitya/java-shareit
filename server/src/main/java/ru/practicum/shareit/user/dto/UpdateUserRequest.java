package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UpdateUserRequest {
    private String name;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
}