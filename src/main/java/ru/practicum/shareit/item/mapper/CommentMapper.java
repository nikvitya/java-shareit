package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.entity.Comment;

@UtilityClass
public class CommentMapper {

    public static CommentResponse toCommentResponse(Comment comment) {
        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        return factory.createProjection(CommentResponse.class, comment);
    }

}
