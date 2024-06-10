package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.follow.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowJpaRepository extends JpaRepository<Follow, Long> {

    @Query("select ifnull(count(f), 0) from Follow f where f.follower.id = :userId")
    int getFollowerCount(@Param("userId") Long userId);

    @Query("select ifnull(count(f), 0) from Follow f where f.followee.id = :userId")
    int getFolloweeCount(@Param("userId") Long userId);

    Optional<Follow> getFollowByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
