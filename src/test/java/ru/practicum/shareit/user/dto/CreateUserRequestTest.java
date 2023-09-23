package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CreateUserRequestTest {
    @Autowired
    private JacksonTester<CreateUserRequest> json;

    @Test
    void testCreateUserRequest() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest().setName("Павел").setEmail("pavel@gmail.com");
        JsonContent<CreateUserRequest> result = json.write(createUserRequest);

        assertThat(result).hasJsonPathValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(createUserRequest.getName());
        assertThat(result).hasJsonPathValue("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(createUserRequest.getEmail());
    }
}