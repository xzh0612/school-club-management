package com.club.vo;

import com.club.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserVOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializationExposesOnlyViewFields() throws Exception {
        User user = new User();
        user.setUserId(12);
        user.setUsername("alice");
        user.setPassword("secret");
        user.setRealName("Alice");
        user.setRole("student");
        user.setStatus("active");
        user.setTokenVersion(5);
        user.setEmail("alice@example.com");

        String json = objectMapper.writeValueAsString(UserVO.from(user));

        assertTrue(json.contains("\"userId\":12"));
        assertTrue(json.contains("\"id\":12"));
        assertTrue(json.contains("\"realName\":\"Alice\""));
        assertFalse(json.contains("password"));
        assertFalse(json.contains("tokenVersion"));
    }
}
