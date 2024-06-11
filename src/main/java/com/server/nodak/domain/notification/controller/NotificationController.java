package com.server.nodak.domain.notification.controller;

import com.server.nodak.domain.follow.service.FollowService;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserRole;
import com.server.nodak.security.aop.AuthorizationRequired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class NotificationController {

    private final Map<Long, SseEmitter> clients = new ConcurrentHashMap<>();

    @Autowired
    private FollowService followService;

    @GetMapping("/subscribe/{userId}")
    @AuthorizationRequired(UserRole.GENERAL)
    public SseEmitter subscribe(Principal principal) {
        long userId = Long.parseLong(principal.getName());

        SseEmitter emitter = new SseEmitter();
        clients.put(userId, emitter);
        emitter.onCompletion(() -> clients.remove(userId));
        emitter.onTimeout(() -> clients.remove(userId));

        return emitter;
    }

    public void notifyFollowers(User user, Post post) {
        followService.getFollowers(user.getId()).forEach(follower -> {
            SseEmitter emitter = clients.get(follower.getUserId());

            if (emitter != null) {
                try {
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", user.getId());
                    data.put("postId", post.getId());
                    data.put("title", post.getTitle());
                    emitter.send(SseEmitter.event().name("newPost").data(data, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    clients.remove(follower.getUserId());
                }
            }
        });
    }
}
