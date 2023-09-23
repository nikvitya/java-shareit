package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CreateUserRequest {
    @NotBlank
    private String name;

    @NotEmpty
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
}