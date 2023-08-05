package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {

    private Integer id;
    private String description;
    private User requestor;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemRequestDto)) return false;
        ItemRequestDto that = (ItemRequestDto) o;
        return Objects.equals(id, that.id) && Objects.equals(description, that.description) && Objects.equals(requestor, that.requestor) && Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, requestor, created);
    }

    @Override
    public String toString() {
        return "ItemRequestDto{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", requestor=" + requestor +
                ", created=" + created +
                '}';
    }
}
