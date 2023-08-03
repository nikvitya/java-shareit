package ru.practicum.shareit.booking.dto.model;

import lombok.Getter;

@Getter
public enum Status {
    WAITING,
    APPROVED,
    REJECTED,
    CANCELED
}
