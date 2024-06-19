package com.server.nodak.domain.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.post.domain.QStarPost;
import com.server.nodak.domain.post.dto.*;
import com.server.nodak.domain.vote.domain.QVoteOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.server.nodak.domain.comment.domain.QComment.comment;
import static com.server.nodak.domain.post.domain.QPost.post;
import static com.server.nodak.domain.post.domain.QStarPost.starPost;
import static com.server.nodak.domain.vote.domain.QVoteHistory.voteHistory;
import static com.server.nodak.domain.vote.domain.QVoteOption.voteOption;

@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostSearchResponse> findMyLike(Long userId, Pageable pageable) {
        List<Long> postIds = queryFactory.select(starPost.post.id).distinct()
                .from(starPost)
                .where(starPost.user.id.eq(userId))
                .fetch();

        List<PostSearchResponse> fetch = queryFactory.select(
                        new QPostSearchResponse(
                                post.id,
                                post.vote.id,
                                post.title,
                                post.comments.size(),
                                post.starPosts.size(),
                                JPAExpressions
                                        .select(voteHistory.count())
                                        .from(voteHistory)
                                        .where(voteHistory.voteOption.in(post.vote.voteOptions)),
                                post.user.nickname,
                                post.user.profileImageUrl,
                                post.imageUrl,
                                post.createdAt,
                                post.vote.startDate,
                                post.vote.endDate,
                                post.vote.isTerminated,
                                Expressions.constant(new ArrayList<>())
                        )
                )
                .from(post)
                .where(post.id.in(postIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        // Fetch voteOptions separately
        Map<Long, List<String>> voteOptionsMap = fetch.stream()
                .collect(Collectors.toMap(
                        PostSearchResponse::getVoteId,
                        response -> queryFactory.select(voteOption.content)
                                .from(voteOption)
                                .where(voteOption.vote.id.eq(response.getVoteId()))
                                .fetch()
                ));

        // Set voteOptions for each PostSearchResponse
        fetch.forEach(response -> {
            List<String> options = voteOptionsMap.get(response.getVoteId());
            response.setVoteOptions(options);
        });

        long count = findMyLikeForCount(userId);

        return new PageImpl<>(fetch, pageable, count);
    }

    private long findMyLikeForCount(Long userId) {
        List<Long> postIds = queryFactory.select(starPost.post.id).distinct()
                .from(starPost)
                .where(starPost.user.id.eq(userId))
                .fetch();

        Long count = queryFactory.select(
                        post.count()
                )
                .from(post)
                .where(post.id.in(postIds))
                .fetchOne();

        if (count == null) {
            count = 0L;
        }

        return count;
    }

    @Override
    public Page<PostSearchResponse> findMyComment(Long userId, Pageable pageable) {
        List<Long> postIds = queryFactory.select(comment.post.id)
                .from(comment)
                .where(comment.user.id.eq(userId))
                .fetch();

        List<PostSearchResponse> fetch = queryFactory.select(
                        new QPostSearchResponse(
                                post.id,
                                post.vote.id,
                                post.title,
                                post.comments.size(),
                                post.starPosts.size(),
                                JPAExpressions
                                        .select(voteHistory.count())
                                        .from(voteHistory)
                                        .where(voteHistory.voteOption.in(post.vote.voteOptions)),
                                post.user.nickname,
                                post.user.profileImageUrl,
                                post.imageUrl,
                                post.createdAt,
                                post.vote.startDate,
                                post.vote.endDate,
                                post.vote.isTerminated,
                                Expressions.constant(new ArrayList<>())
                        )
                )
                .from(post)
                .where(post.id.in(postIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        // Fetch voteOptions separately
        Map<Long, List<String>> voteOptionsMap = fetch.stream()
                .collect(Collectors.toMap(
                        PostSearchResponse::getVoteId,
                        response -> queryFactory.select(voteOption.content)
                                .from(voteOption)
                                .where(voteOption.vote.id.eq(response.getVoteId()))
                                .fetch()
                ));

        // Set voteOptions for each PostSearchResponse
        fetch.forEach(response -> {
            List<String> options = voteOptionsMap.get(response.getVoteId());
            response.setVoteOptions(options);
        });

        long count = findMyCommentForCount(userId);

        return new PageImpl<>(fetch, pageable, count);
    }

    private long findMyCommentForCount(Long userId) {
        List<Long> postIds = queryFactory.select(comment.post.id)
                .from(comment)
                .where(comment.user.id.eq(userId))
                .fetch();

        Long count = queryFactory.select(
                        post.count()
                )
                .from(post)
                .where(post.id.in(postIds))
                .fetchOne();

        if (count == null) {
            count = 0L;
        }

        return count;
    }

    @Override
    public Page<PostSearchResponse> findMyVoteHistory(Long userId, Pageable pageable) {
        List<Long> voteIds = queryFactory.select(voteHistory.voteOption.vote.id)
                .from(voteHistory)
                .where(voteHistory.user.id.eq(userId))
                .fetch();

        List<PostSearchResponse> fetch = queryFactory.select(
                        new QPostSearchResponse(
                                post.id,
                                post.vote.id,
                                post.title,
                                post.comments.size(),
                                post.starPosts.size(),
                                JPAExpressions
                                        .select(voteHistory.count())
                                        .from(voteHistory)
                                        .where(voteHistory.voteOption.in(post.vote.voteOptions)),
                                post.user.nickname,
                                post.user.profileImageUrl,
                                post.imageUrl,
                                post.createdAt,
                                post.vote.startDate,
                                post.vote.endDate,
                                post.vote.isTerminated,
                                Expressions.constant(new ArrayList<>())
                        )
                )
                .from(post)
                .where(post.vote.id.in(voteIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        // Fetch voteOptions separately
        Map<Long, List<String>> voteOptionsMap = fetch.stream()
                .collect(Collectors.toMap(
                        PostSearchResponse::getVoteId,
                        response -> queryFactory.select(voteOption.content)
                                .from(voteOption)
                                .where(voteOption.vote.id.eq(response.getVoteId()))
                                .fetch()
                ));

        // Set voteOptions for each PostSearchResponse
        fetch.forEach(response -> {
            List<String> options = voteOptionsMap.get(response.getVoteId());
            response.setVoteOptions(options);
        });

        long count = findMyVoteHistoryForCount(userId);

        return new PageImpl<>(fetch, pageable, count);
    }

    private long findMyVoteHistoryForCount(Long userId) {
        List<Long> voteIds = queryFactory.select(voteHistory.voteOption.vote.id)
                .from(voteHistory)
                .where(voteHistory.user.id.eq(userId))
                .fetch();

        Long count = queryFactory.select(
                        post.count()
                )
                .from(post)
                .where(post.vote.id.in(voteIds))
                .fetchOne();

        if (count == null) {
            count = 0L;
        }

        return count;
    }

    @Override
    public Page<PostSearchResponse> findMyPosting(Long userId, Pageable pageable) {
        List<PostSearchResponse> fetch = queryFactory.select(
                        new QPostSearchResponse(
                                post.id,
                                post.vote.id,
                                post.title,
                                post.comments.size(),
                                post.starPosts.size(),
                                JPAExpressions
                                        .select(voteHistory.count())
                                        .from(voteHistory)
                                        .where(voteHistory.voteOption.in(post.vote.voteOptions)),
                                post.user.nickname,
                                post.user.profileImageUrl,
                                post.imageUrl,
                                post.createdAt,
                                post.vote.startDate,
                                post.vote.endDate,
                                post.vote.isTerminated,
                                Expressions.constant(new ArrayList<>())
                        )
                )
                .from(post)
                .where(post.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        // Fetch voteOptions separately
        Map<Long, List<String>> voteOptionsMap = fetch.stream()
                .collect(Collectors.toMap(
                        PostSearchResponse::getVoteId,
                        response -> queryFactory.select(voteOption.content)
                                .from(voteOption)
                                .where(voteOption.vote.id.eq(response.getVoteId()))
                                .fetch()
                ));

        // Set voteOptions for each PostSearchResponse
        fetch.forEach(response -> {
            List<String> options = voteOptionsMap.get(response.getVoteId());
            response.setVoteOptions(options);
        });

        long count = findMyPostingForCount(userId);

        return new PageImpl<>(fetch, pageable, count);
    }

    private long findMyPostingForCount(Long userId) {
        Long count = queryFactory.select(
                        post.count()
                )
                .from(post)
                .where(post.user.id.eq(userId))
                .fetchOne();

        if (count == null) {
            count = 0L;
        }

        return count;
    }

    @Override
    public Optional<PostResponse> findOne(Long userId, Long postId) {
        QStarPost starPost = QStarPost.starPost;
        PostResponse postResponse = queryFactory.select(
                        new QPostResponse(
                                post.title,
                                post.user.nickname,
                                userId != null ?
                                        post.user.id.eq(userId) : Expressions.FALSE,
                                post.comments.size(),
                                post.user.profileImageUrl,
                                post.createdAt,
                                post.content,
                                post.imageUrl,
                                post.starPosts.size(),
                                userId != null ?
                                        post.starPosts.contains(
                                                JPAExpressions.selectFrom(starPost)
                                                        .where(starPost.user.id.eq(userId),
                                                                starPost.post.id.eq(postId))
                                        ) : Expressions.FALSE
                        )
                )
                .from(post)
                .where(post.id.eq(postId))
                .fetchOne();
        return Optional.ofNullable(postResponse);
    }

    @Override
    public Page<PostSearchResponse> search(PostSearchRequest request, Pageable pageable) {
        QVoteOption voteOption = QVoteOption.voteOption;

        List<PostSearchResponse> fetch = queryFactory.select(
                        new QPostSearchResponse(
                                post.id,
                                post.vote.id,
                                post.title,
                                post.comments.size(),
                                post.starPosts.size(),
                                JPAExpressions
                                        .select(voteHistory.count())
                                        .from(voteHistory)
                                        .where(voteHistory.voteOption.in(post.vote.voteOptions)),
                                post.user.nickname,
                                post.user.profileImageUrl,
                                post.imageUrl,
                                post.createdAt,
                                post.vote.startDate,
                                post.vote.endDate,
                                post.vote.isTerminated,
                                Expressions.constant(new ArrayList<>())
                        )
                )
                .from(post)
                .where(
                        searchCondition(request),
                        searchCategory(request)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        // Fetch voteOptions separately
        Map<Long, List<String>> voteOptionsMap = fetch.stream()
                .collect(Collectors.toMap(
                        PostSearchResponse::getVoteId,
                        response -> queryFactory.select(voteOption.content)
                                .from(voteOption)
                                .where(voteOption.vote.id.eq(response.getVoteId()))
                                .fetch()
                ));

        // Set voteOptions for each PostSearchResponse
        fetch.forEach(response -> {
            List<String> options = voteOptionsMap.get(response.getVoteId());
            response.setVoteOptions(options);
        });

        long count = searchForCount(request);

        return new PageImpl<>(fetch, pageable, count);
    }

    private long searchForCount(PostSearchRequest request) {
        Long count = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        searchCondition(request),
                        searchCategory(request)
                )
                .fetchOne();

        if (count == null) {
            count = 0L;
        }

        return count;
    }

    private BooleanBuilder searchCondition(PostSearchRequest request) {
        return new BooleanBuilder()
                .and(titleContains(request.getKeyword()))
                .or(contentContains(request.getKeyword()));
    }

    private BooleanBuilder searchCategory(PostSearchRequest request) {
        return new BooleanBuilder()
                .and(catergoryIdEq(request.getCategoryId()));
    }

    private BooleanExpression titleContains(String title) {
        return title != null ? post.title.contains(title) : null;
    }

    private BooleanExpression contentContains(String content) {
        return content != null ? post.content.contains(content) : null;
    }

    private BooleanExpression catergoryIdEq(Long categoryId) {
        return categoryId != null ? post.category.id.eq(categoryId) : null;
    }
}
