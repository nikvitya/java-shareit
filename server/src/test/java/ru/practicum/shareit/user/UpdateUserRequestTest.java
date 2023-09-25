package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UpdateUserRequestTest {
    @Autowired
    private JacksonTester<UpdateUserRequest> json;

    @Test
    void testUpdateUserRequest() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest().setName("Паша").setEmail("pasha@gmail.com");
        JsonContent<UpdateUserRequest> result = json.write(updateUserRequest);

        assertThat(result).hasJsonPathValue("$.name");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(updateUserRequest.getName());
        assertThat(result).hasJsonPathValue("$.email");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(updateUserRequest.getEmail());
    }
}