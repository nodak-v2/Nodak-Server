package com.server.nodak.domain.chat.repository;

import static com.server.nodak.domain.chat.domain.QChatRoom.chatRoom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.chat.dto.response.ChatRoomListResponse;
import com.server.nodak.domain.chat.dto.response.QChatRoomListResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChatRoomListResponse> findChatRoom(long requesterId, Pageable pageable) {
        List<ChatRoomListResponse> fetch = queryFactory.select(
                        new QChatRoomListResponse(
                                chatRoom.id,
                                Expressions.stringTemplate("concat({0}, ' 사용와의 채팅방')", chatRoom.acceptor.nickname)
                        )
                )
                .from(chatRoom)
                .where(
                        buildRequesterIdCondition(requesterId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = findChatRoomForCount(requesterId);
        return new PageImpl<>(fetch, pageable, count);
    }

    private Long findChatRoomForCount(long requesterId) {
        Long count = queryFactory.select(
                        chatRoom.count()
                )
                .from(chatRoom)
                .where(
                        buildRequesterIdCondition(requesterId)
                ).fetchOne();

        if (count == null) {
            count = 0L;
        }

        return count;
    }

    private BooleanBuilder buildRequesterIdCondition(Long requesterId) {
        return new BooleanBuilder()
                .and(matchRequestor(requesterId))
                .or(matchAcceptor(requesterId));
    }

    private BooleanExpression matchAcceptor(Long requesterId) {
        return requesterId != null ? chatRoom.acceptor.id.eq(requesterId) : null;
    }

    private BooleanExpression matchRequestor(Long requesterId) {
        return requesterId != null ? chatRoom.requester.id.eq(requesterId) : null;
    }

}
