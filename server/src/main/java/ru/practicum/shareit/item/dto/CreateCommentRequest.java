package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class CreateCommentRequest {

    private String text;
}
