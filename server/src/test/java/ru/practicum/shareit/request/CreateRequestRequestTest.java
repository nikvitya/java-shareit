package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.CreateRequestRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateRequestRequestTest {
    @Autowired
    private JacksonTester<CreateRequestRequest> json;

    @Test
    void testCreateRequestRequest() throws Exception {
        CreateRequestRequest createRequestRequest = new CreateRequestRequest().setDescription("нужна ударная дрель");
        JsonContent<CreateRequestRequest> result = json.write(createRequestRequest);

        assertThat(result).hasJsonPathValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(createRequestRequest.getDescription());
    }

}