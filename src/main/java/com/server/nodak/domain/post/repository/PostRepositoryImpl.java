package com.server.nodak.domain.post.repository;

import static com.server.nodak.domain.post.domain.QPost.post;
import static com.server.nodak.domain.vote.domain.QVoteHistory.voteHistory;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.post.dto.QPostSearchResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostSearchResponse> search(PostSearchRequest request, Pageable pageable) {
        List<PostSearchResponse> fetch = queryFactory.select(
                        new QPostSearchResponse(
                                post.id,
                                post.title,
                                JPAExpressions
                                        .select(voteHistory.count())
                                        .from(voteHistory)
                                        .where(voteHistory.voteOption.in(post.vote.voteOptions)),
                                post.user.nickname,
                                post.user.profileImageUrl,
                                post.imageUrl
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

        long count = searchForCount(request);
        return new PageImpl<>(fetch, pageable, count);
    }

    private long searchForCount(PostSearchRequest request) {
        Long count = queryFactory
                .select(post.count())
                .from(post)
                .where(searchCondition(request))
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
