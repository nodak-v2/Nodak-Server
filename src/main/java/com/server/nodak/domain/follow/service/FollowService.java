package com.server.nodak.domain.follow.service;

import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.follow.repository.FollowRepository;
import com.server.nodak.domain.notification.service.NotificationService;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.UserInfoDTO;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.BadRequestException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public long getUserFollowerCount(Long userId) {
        return followRepository.getUserFollowerCount(userId);
    }

    @Transactional(readOnly = true)
    public long getUserFolloweeCount(Long userId) {
        return followRepository.getUserFolloweeCount(userId);
    }

    @Transactional
    public void followUser(Long userId, Long followeeId) {
        if (userId == followeeId) {
            throw new BadRequestException();
        }
        User follower = checkIfUserExists(userId);
        User followee = checkIfUserExists(followeeId);

        Optional<Follow> followOptional = followRepository.checkIfDeletedFollowExists(userId, followeeId);

        if (followOptional.isPresent()) {
            followOptional.get().updateDelete(false);
            redisTemplate.opsForSet().add(String.valueOf(follower.getId()) + ":followee", followee.getId());
            redisTemplate.opsForSet().add(String.valueOf(followee.getId()) + ":follower", follower.getId());
            notificationService.saveFollowNotification(follower, followee);
            return;
        }

        if (followRepository.isFollowing(userId, followeeId)) {
            throw new BadRequestException("Already following this user.");
        }

        Follow follow = Follow.create(follower, followee);
        followRepository.save(follow);
        notificationService.saveFollowNotification(follower, followee);
    }

    private User checkIfUserExists(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException());
    }

    @Transactional
    public void unfollowUser(Long userId, Long followeeId) {
        if (userId == followeeId) {
            throw new BadRequestException();
        }
        Follow follow = followRepository.getFollowByRelation(userId, followeeId)
            .orElseThrow(() -> new BadRequestException("follow not found"));

        checkIfUserExists(userId);
        checkIfUserExists(followeeId);

        follow.updateDelete(true);
        followRepository.deleteFromRedis(follow.getFollower().getId(), followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserInfoDTO> getFollowers(Long myId, Long userId) {
        return followRepository.getFollowersByUserId(myId, userId);
    }

    @Transactional(readOnly = true)
    public List<UserInfoDTO> getFollowees(Long myId, Long userId) {
        return followRepository.getFolloweesByUserId(myId, userId);
    }
}
