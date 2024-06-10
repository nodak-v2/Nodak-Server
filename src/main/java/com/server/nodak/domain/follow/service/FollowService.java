package com.server.nodak.domain.follow.service;

import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.follow.repository.FollowRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.dto.UserInfoResponse;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public int getUserFollowerCount(Long userId) {
        return followRepository.getUserFollowerCount(userId);
    }

    @Transactional(readOnly = true)
    public int getUserFolloweeCount(Long userId) {
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
            return;
        }

        if (followRepository.getFollowByRelation(userId, followeeId).isPresent()) {
            throw new BadRequestException("Already following this user.");
        }

        Follow follow = Follow.create(follower, followee);
        followRepository.save(follow);
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
        followRepository.save(follow);
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getFollowers(Long userId) {
        List<User> followers = followRepository.getFollowersByUserId(userId);

        int followerCount = followRepository.getUserFollowerCount(userId);
        int followeeCount = followRepository.getUserFolloweeCount(userId);

        return followers.stream()
                .map(user -> UserInfoResponse.of(user, followerCount, followeeCount))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserInfoResponse> getFollowees(Long userId) {
        List<User> followees = followRepository.getFolloweesByUserId(userId);

        int followerCount = followRepository.getUserFollowerCount(userId);
        int followeeCount = followRepository.getUserFolloweeCount(userId);

        return followees.stream()
                .map(user -> UserInfoResponse.of(user, followerCount, followeeCount))
                .collect(Collectors.toList());
    }
}
