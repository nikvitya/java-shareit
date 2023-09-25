package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentResponse> findByItem_IdOrderByCreatedAsc(long itemId);

}

