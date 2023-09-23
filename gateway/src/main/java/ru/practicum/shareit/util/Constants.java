package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final Sort SORT_BY_START_DESC = Sort.by("start").descending();
    public static final Sort SORT_BY_CREATED_DESC = Sort.by("created").descending();
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
}