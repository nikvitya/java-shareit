package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    /*  id — уникальный идентификатор запроса;
    description — текст запроса, содержащий описание требуемой вещи;
    requestor — пользователь, создавший запрос;
    created — дата и время создания запроса.*/

    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;

}
