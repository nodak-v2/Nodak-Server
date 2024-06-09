package com.server.nodak.domain.follow.repository;

import com.server.nodak.domain.follow.domain.Follow;
import com.server.nodak.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowJpaRepository extends JpaRepository<Follow, Long> {

    @Query("select ifnull(count(f), 0) from Follow f where f.follower = :userId")
    int getFollowerCount(@Param("userId") Long userId);

    @Query("select ifnull(count(f), 0) from Follow f where f.followee = :userId")
    int getFolloweeCount(@Param("userId") Long userId);

    Optional<Follow> getFollowByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    @Query("select f.follower from Follow f where f.followee.id = :userId and f.isDeleted = false")
    List<User> findFollowersByUserId(@Param("userId") Long userId);

    @Query("select f.followee from Follow f where f.follower.id = :userId and f.isDeleted = false")
    List<User> findFolloweesByUserId(@Param("userId") Long userId);
}
