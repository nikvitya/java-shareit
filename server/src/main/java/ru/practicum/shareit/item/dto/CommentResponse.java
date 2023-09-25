package ru.practicum.shareit.item.dto;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface CommentResponse {

    Long getId();

    String getText();

    @Value("#{target.getAuthor.getName}")
    String getAuthorName();

    LocalDateTime getCreated();
}
