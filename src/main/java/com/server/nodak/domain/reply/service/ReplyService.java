package com.server.nodak.domain.reply.service;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.comment.repository.CommentRepository;
import com.server.nodak.domain.reply.dto.CreateReplyRequest;
import com.server.nodak.domain.reply.dto.DeleteReplyRequest;
import com.server.nodak.domain.reply.dto.ReplyResponse;
import com.server.nodak.domain.reply.entity.Reply;
import com.server.nodak.domain.reply.repository.ReplyRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.BadRequestException;
import com.server.nodak.exception.common.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReplyResponse createReply(long userId, CreateReplyRequest replyRequest) {
        Long commentId = replyRequest.getCommentId();
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new DataNotFoundException());

        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException());
        Reply reply = Reply.builder()
                .comment(comment)
                .user(user)
                .content(replyRequest.getContent())
                .build();

        replyRepository.save(reply);

        return new ReplyResponse(reply);
    }

    @Transactional
    public void deleteReply(long userId, DeleteReplyRequest deleteRequest) {
        Reply reply = replyRepository.findById(deleteRequest.getReplyId()).orElseThrow(() -> new BadRequestException());

        if (userId != reply.getUser().getId()) {
            throw new BadRequestException("유저가 작성한 대댓글이 아닙니다.");
        }

        reply.setDeleted(true);
    }

    public List<ReplyResponse> getAllByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BadRequestException());
        List<Reply> replies = comment.getReplies();

        List<ReplyResponse> result = new ArrayList<>();

        for (Reply reply : replies) {
            ReplyResponse replyResponse = new ReplyResponse(reply);

            if (reply.isDeleted()) {
                replyResponse.setContent("삭제된 댓글입니다.");;
            }
            result.add(replyResponse);
        }
        return result;
    }
}
