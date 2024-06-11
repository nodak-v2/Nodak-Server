package com.server.nodak.domain.notification.service;

import com.server.nodak.domain.follow.service.FollowService;
import com.server.nodak.domain.notification.entity.Notification;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.AuthorizationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOps;
    private final FollowService followService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public SseEmitter getSseEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(15 * 60 * 1000L);
        clients.put(userId, emitter);

        emitter.onCompletion(() -> clients.remove(userId));
        emitter.onTimeout(() -> clients.remove(userId));

        User user = userRepository.findById(userId).orElseThrow(() -> new AuthorizationException());
        try {
            emitter.send(SseEmitter.event().name("init").data("Connected"));
            List<Notification> notifications = notificationService.getNotifications(userId);

            for (Notification notification : notifications) {
                Map<String, Object> data = new HashMap<>();
                data.put("userId", notification.getWriterId());
                data.put("nickname", user.getNickname());
                data.put("postId", notification.getPostId());
                emitter.send(SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
            }
        } catch (IOException e) {
            clients.remove(userId);
        }

        return emitter;
    }

    /**
     * user 의 팔로워들에게, Post 알림 전송 */
    public void notifyFollowers(User user, Post post) {
        followService.getFollowers(user.getId()).forEach(follower -> {
            SseEmitter emitter = clients.get(follower.getUserId());

            // Redis에 알림 저장
            notificationService.saveNotificationToRedis(follower.getUserId(), user, post);

            if (emitter != null) {
                try {
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", user.getId());
                    data.put("nickname", user.getNickname());
                    data.put("postId", post.getId());
                    emitter.send(SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    clients.remove(follower.getUserId());
                }
            }
        });
    }

    public void saveNotificationToRedis(Long followerId, User writer, Post post) {
        Notification notification = new Notification(writer, post);
        String key = "notification:"+ followerId + ":" + System.currentTimeMillis();
        valueOps.set(key, notification, 3, TimeUnit.DAYS);
    }

    public List<Notification> getNotifications(Long userId) {
        // 키 패턴을 사용하여 모든 알림 가져오기
        // TODO : SCAN 처리
        Set<String> keys = redisTemplate.keys("notification:" + userId + ":*");
        return keys.stream()
                .map(key -> (Notification) valueOps.get(key))
                .collect(Collectors.toList());
    }

    public void deleteNotifications(Long userId) {
        Set<String> keys = redisTemplate.keys("notification:" + userId + ":*");
        redisTemplate.delete(keys);
    }
}
