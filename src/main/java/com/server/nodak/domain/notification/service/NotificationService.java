package com.server.nodak.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.nodak.domain.follow.service.FollowService;
import com.server.nodak.domain.notification.entity.Notification;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final FollowService followService;

    /**
     * SSE 연결 시, 동작 */
    public SseEmitter getSseEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(15 * 60 * 1000L);
        clients.put(userId, emitter);

        emitter.onCompletion(() -> clients.remove(userId));
        emitter.onTimeout(() -> clients.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("init").data("Connected"));
            List<Notification> notifications = getUndeliveredNotifications(userId);

            for (Notification notification : notifications) {
                Map<String, Object> data = new HashMap<>();
                data.put("postId", notification.getPostId());
                data.put("message", notification.getMessage());
                emitter.send(SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
            }
        } catch (IOException e) {
            clients.remove(userId);
        }

        return emitter;
    }

    public void saveNotificationToRedis(Long postId, String message, Long writerId) {
        Notification notification = new Notification(postId, message, writerId);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String notificationJson = objectMapper.writeValueAsString(notification);
            redisTemplate.opsForZSet().add("notifications", notificationJson, notification.getTimestamp());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    // TODO: SCAN 을 통한 성능 개선
    public List<Notification> getUndeliveredNotifications(Long userId) {
        List<Long> followingIds = followService.getFollowees(userId).stream()
                .map(UserInfoResponse::getUserId)
                .collect(Collectors.toList());

        List<Notification> notifications = new ArrayList<>();
        long oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L;

        // Sorted Set에서 1주일 이내의 값을 가져오기
        Set<Object> notificationJsons = redisTemplate.opsForZSet().rangeByScore("notifications", oneWeekAgo, Double.MAX_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        for (Object notificationJson : notificationJsons) {
            try {
                Notification notification = objectMapper.readValue(notificationJson.toString(), Notification.class);
                if (notification != null && followingIds.contains(notification.getWriterId())) {
                    notifications.add(notification);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        return notifications;
    }

    /**
     * user 의 팔로워들에게, Post 알림 전송 */
    public void notifyFollowersBySse(User user, Post post) {
        followService.getFollowers(user.getId()).forEach(follower -> {
            SseEmitter emitter = clients.get(follower.getUserId());
            if (emitter != null) {
                try {
                    Map<String, Object> data = new HashMap<>();
                    data.put("postId", post.getId());
                    data.put("message", user.getNickname() + "님이 새로운 게시글을 작성했습니다.");
                    emitter.send(SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    clients.remove(follower.getUserId());
                }
            }
        });
    }
}
