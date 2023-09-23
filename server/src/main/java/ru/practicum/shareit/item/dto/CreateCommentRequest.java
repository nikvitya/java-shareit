package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class CreateCommentRequest {

    @NotBlank
    private String text;
}
