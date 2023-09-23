package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.util.Constants.DATE_TIME_FORMATTER;

@JsonTest
class CreateRequestResponseTest {
    @Autowired
    JacksonTester<CreateRequestResponse> json;

    @Test
    void testCreateRequestResponse() throws Exception {
        CreateRequestResponse createRequestResponse = new CreateRequestResponse(1L, "нужна ударная дрель",
                LocalDateTime.now());
        JsonContent<CreateRequestResponse> result = json.write(createRequestResponse);

        assertThat(result).hasJsonPathValue("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(createRequestResponse.getId().intValue());
        assertThat(result).hasJsonPathValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(createRequestResponse.getDescription());
        assertThat(result).hasJsonPathValue("$.created");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(createRequestResponse.getCreated().format(DATE_TIME_FORMATTER));
    }
}