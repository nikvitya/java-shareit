package ru.practicum.shareit.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CreateRequestRequest {
    @NotBlank
    private String description;
}