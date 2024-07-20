package com.server.nodak.domain.notification.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FollowEventTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSerializeDeserialize() {
        FollowEvent original = new FollowEvent(1L, "닉네임1", 2L, "닉네임2", 1718555944013L);
        try {
            String json = objectMapper.writeValueAsString(original);
            System.out.println("Serialized JSON: " + json);
            FollowEvent deserialized = objectMapper.readValue(json, FollowEvent.class);
            assertEquals(original.getFollowerId(), deserialized.getFollowerId());
            assertEquals(original.getFollowerName(), deserialized.getFollowerName());
            assertEquals(original.getFolloweeId(), deserialized.getFolloweeId());
            assertEquals(original.getFolloweeName(), deserialized.getFolloweeName());
            assertEquals(original.getTimestamp(), deserialized.getTimestamp());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Serialization or deserialization failed: " + e.getMessage());
        }
    }
}