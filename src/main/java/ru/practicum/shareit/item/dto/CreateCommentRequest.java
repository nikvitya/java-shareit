package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateCommentRequest {

    @NotBlank
    private String text;
}
