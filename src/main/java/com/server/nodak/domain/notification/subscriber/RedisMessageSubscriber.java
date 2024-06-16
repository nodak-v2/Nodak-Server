package com.server.nodak.domain.notification.subscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.nodak.domain.notification.event.FollowEvent;
import com.server.nodak.domain.notification.event.PostEvent;
import com.server.nodak.domain.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final FollowRepository followRepository;
    private static Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    public static void addClient(Long userId, SseEmitter emitter) {
        clients.put(userId, emitter);
    }

    public static void removeClient(Long userId) {
        clients.remove(userId);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 메시지 채널을 확인하여 적절한 메서드 호출
        String channel = new String(pattern);
        String messageBody = new String(message.getBody());

        if ("postEvent-channel".equals(channel)) {
            onPostMessage(messageBody);
        } else if ("followEvent-channel".equals(channel)) {
            onFollowMessage(messageBody);
        }
        try {
            PostEvent postEvent = new ObjectMapper().readValue(messageBody, PostEvent.class);
            notifyClients(postEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void onPostMessage(String messageBody) {
        try {
            PostEvent postEvent = new ObjectMapper().readValue(messageBody, PostEvent.class);
            notifyClients(postEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void onFollowMessage(String messageBody) {
        try {
            FollowEvent followEvent = new ObjectMapper().readValue(messageBody, FollowEvent.class);
            notifyClients(followEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void notifyClients(PostEvent postEvent) {
        clients.forEach((userId, emitter) -> {
            if (isFollowing(userId, postEvent.getWriterId())) {
                try {
                    Map<String, Object> data = new HashMap<>();
                    data.put("postId", postEvent.getPostId());
                    data.put("message", postEvent.getWriterName() + "님이 새로운 게시글을 작성했습니다.");
                    emitter.send(SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    clients.remove(userId);
                }
            }
        });
    }

    @Async
    public void notifyClients(FollowEvent followEvent) {
        clients.forEach((userId, emitter) -> {
            if (userId.equals(followEvent.getFolloweeId())) {
                try {
                    Map<String, Object> data = new HashMap<>();
                    data.put("followerId", followEvent.getFollowerId());
                    data.put("message", followEvent.getFollowerName() + "님이 당신을 팔로우했습니다.");
                    emitter.send(SseEmitter.event().name("newFollower").data(data, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    clients.remove(userId);
                }
            }
        });
    }

    private boolean isFollowing(Long followerId, Long writerId) {
        return followRepository.isFollowing(followerId, writerId);
    }
}
