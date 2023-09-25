package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@NoArgsConstructor
public class CreateRequestRequest {

    private String description;
}