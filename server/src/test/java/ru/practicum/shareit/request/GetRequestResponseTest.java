package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.GetRequestResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.util.Constants.DATE_TIME_FORMATTER;

@JsonTest
class GetRequestResponseTest {
    @Autowired
    JacksonTester<GetRequestResponse> json;

    @Test
    void testGetRequestResponse() throws Exception {
        GetRequestResponse getRequestResponse = new GetRequestResponse(1L, "нужна ударная дрель",
                LocalDateTime.now(), List.of(new ItemResponse(1L, "Дрель", "ударная",
                true, 1L)));
        JsonContent<GetRequestResponse> result = json.write(getRequestResponse);

        assertThat(result).hasJsonPathValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(getRequestResponse.getId().intValue());
        assertThat(result).hasJsonPathValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(getRequestResponse.getDescription());
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(getRequestResponse.getCreated().format(DATE_TIME_FORMATTER));
        assertThat(result).hasJsonPathValue("$.items");
    }
}