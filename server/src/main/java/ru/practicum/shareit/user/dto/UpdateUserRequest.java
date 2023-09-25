package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UpdateUserRequest {
    private String name;

    private String email;
}