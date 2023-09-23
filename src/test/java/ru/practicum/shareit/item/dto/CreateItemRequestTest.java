package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CreateItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateItemRequestTest {
    @Autowired
    private JacksonTester<CreateItemRequest> json;

    @Test
    void testCreateItemRequest() throws Exception {
        CreateItemRequest createItemRequest =
                new CreateItemRequest("Дрель", "ударная", true,1L);
        JsonContent<CreateItemRequest> result = json.write(createItemRequest);

        assertThat(result).hasJsonPathValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(createItemRequest.getName());
        assertThat(result).hasJsonPathValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(createItemRequest.getDescription());
        assertThat(result).hasJsonPathValue("$.available");
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(createItemRequest.getAvailable());
        assertThat(result).hasJsonPathValue("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(createItemRequest.getRequestId().intValue());
    }
}