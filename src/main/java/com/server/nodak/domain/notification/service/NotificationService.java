package com.server.nodak.domain.notification.service;

import com.server.nodak.domain.follow.repository.FollowRepository;
import com.server.nodak.domain.follow.service.FollowService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.server.nodak.domain.notification.NotificationType;
import com.server.nodak.domain.notification.dto.NotificationInfo;
import com.server.nodak.domain.notification.entity.Notification;
import com.server.nodak.domain.notification.repository.NotificationRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import com.server.nodak.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;

    /**
     * SSE 연결 시, 동작
     */
    public SseEmitter getSseEmitter(Long userId) {
//        SseEmitter emitter = new SseEmitter(15 * 60 * 1000L);
//        clients.put(userId, emitter);
//
//        emitter.onCompletion(() -> clients.remove(userId));
//        emitter.onTimeout(() -> clients.remove(userId));
//
//        try {
//            emitter.send(SseEmitter.event().name("init").data("Connected"));
//            List<Notification> notifications = getUndeliveredNotifications(userId);
//
//            for (Notification notification : notifications) {
//                Map<String, Object> data = new HashMap<>();
//                data.put("postId", notification.getPostId());
//                data.put("message", notification.getMessage());
//                emitter.send(
//                    SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
//            }
//        } catch (IOException e) {
//            clients.remove(userId);
//        }
//
//        return emitter;
//    }
//
//    public void saveNotificationToRedis(Long postId, String message, Long writerId) {
//        Notification notification = new Notification(postId, message, writerId);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String notificationJson = objectMapper.writeValueAsString(notification);
//            redisTemplate.opsForZSet()
//                .add("notifications", notificationJson, notification.getTimestamp());
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public List<NotificationInfo> getNotifications(long userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    public void savePostNotification(User writer, Long postId) {
        List<Long> followerIds = followRepository.getFollowerIds(writer.getId());

        // 모든 알림을 저장할 리스트
        List<Notification> notifications = new ArrayList<>();

        for (Long userId : followerIds) {
            Notification notification = Notification.builder()
                    .type(NotificationType.POST)
                    .userId(userId)
                    .writer(writer)
                    .postId(postId)
                    .build();
            notifications.add(notification);
        }

        notificationRepository.saveAll(notifications);
    }

    public void saveFollowNotification(User follower, User followee) {
        Notification notification = Notification.builder()
                .type(NotificationType.FOLLOW)
                .userId(followee.getId())
                .follower(follower)
                .build();

        notificationRepository.save(notification);
    }

//    public List<Notification> getUndeliveredNotifications(Long userId) {
//        List<Long> followingIds = followService.getFollowees(userId).stream()
//                .map(UserInfoDTO::getUserId)
//                .collect(Collectors.toList());
//
//        List<Notification> notifications = new ArrayList<>();
//        long oneWeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L;
//
//        // Sorted Set에서 1주일 이내의 값을 가져오기
//        Set<Object> notificationJsons = redisTemplate.opsForZSet()
//                .rangeByScore("notifications", oneWeekAgo, Double.MAX_VALUE);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        for (Object notificationJson : notificationJsons) {
//            try {
//                Notification notification = objectMapper.readValue(notificationJson.toString(), Notification.class);
//                if (notification != null && followingIds.contains(notification.getWriterId())) {
//                    notifications.add(notification);
//                }
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return notifications;
//    }

    /**
     * user 의 팔로워들에게, Post 알림 전송
     */
//    public void notifyFollowersBySse(User user, Post post) {
//        followService.getFollowers(user.getId()).forEach(follower -> {
//            SseEmitter emitter = clients.get(follower.getUserId());
//            if (emitter != null) {
//                try {
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("postId", post.getId());
//                    data.put("message", user.getNickname() + "님이 새로운 게시글을 작성했습니다.");
//                    emitter.send(
//                        SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
//                } catch (IOException e) {
//                    clients.remove(follower.getUserId());
//                }
//            }
//        });
//    }
}
