package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CreateCommentRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateCommentRequestTest {

    @Autowired
    private JacksonTester<CreateCommentRequest> json;

    @Test
    void testCreateCommentRequest() throws Exception {
        CreateCommentRequest createCommentRequest = new CreateCommentRequest().setText("Супер!");
        JsonContent<CreateCommentRequest> result = json.write(createCommentRequest);

        assertThat(result).hasJsonPathValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(createCommentRequest.getText());
    }
}