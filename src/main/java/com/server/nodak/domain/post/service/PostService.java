package com.server.nodak.domain.post.service;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.domain.StarPost;
import com.server.nodak.domain.post.dto.PostRequest;
import com.server.nodak.domain.post.dto.PostResponse;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.post.repository.CategoryRepository;
import com.server.nodak.domain.post.repository.PostRepository;
import com.server.nodak.domain.post.repository.StarPostRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.exception.common.AuthorizationException;
import com.server.nodak.exception.common.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final CategoryRepository categoryRepository;

    private final StarPostRepository starPostRepository;

    @Transactional
    public void savePost(Long userId, PostRequest request) {
        User user = findUserById(userId);
        Category category = findCategoryByTitle(request.getChannel());
        Post post = createPost(user, category, request);
        Vote vote = createVote(post, request.getVoteTitle());
        List<VoteOption> list = request.getVoteOptionContent().entrySet().stream().map(e ->
                createVoteOption(e.getKey(), e.getValue(), vote)
        ).toList();

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchResponse> findPostBySearch(PostSearchRequest request, Pageable pageable) {
        return postRepository.search(request, pageable);
    }

    @Transactional(readOnly = true)
    public PostResponse findPostOne(Long userId, Long postId) {
        return postRepository.findOne(userId, postId);
    }

    @Transactional
    public void updatePost(Long userId, Long postId, PostRequest request) {
        Post findPost = findPostByIdAndUserId(postId, userId);
        findPost.update(request);
        postRepository.save(findPost);
    }

    @Transactional
    public void removePost(Long userId, Long postId) {
        Post findPost = findPostByIdAndUserId(postId, userId);
        findPost.delete(true);
        postRepository.save(findPost);
    }

    @Transactional
    public void registerLike(Long userId, Long postId) {
        StarPost starPost = createStarPost(findUserById(userId), findPostById(postId));
        starPostRepository.save(starPost);
    }

    @Transactional
    public void cancleLike(Long userId, Long postId) {
        StarPost starPost = starPostRepository.findByDeletedIsTrue(userId, postId)
                .orElseThrow(() -> new BadRequestException());
        starPost.delete(true);
        starPostRepository.save(starPost);
    }

    private VoteOption createVoteOption(int seq, String content, Vote vote) {
        return VoteOption.builder()
                .seq(seq)
                .content(content)
                .vote(vote)
                .build();
    }

    private Post createPost(User user, Category category, PostRequest req) {
        return Post.builder()
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .category(category)
                .imageUrl(req.getImageUrl())
                .build();
    }

    private Vote createVote(Post post, String title) {
        return Vote.builder()
                .post(post)
                .title(title)
                .build();
    }

    private StarPost createStarPost(User user, Post post) {
        return StarPost.builder().user(user).post(post).build();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new AuthorizationException());
    }

    private Category findCategoryByTitle(String channel) {
        return categoryRepository.findByTitle(channel).orElseThrow(() -> new BadRequestException("존재하지 않는 카테고리입니다."));
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new BadRequestException("존재하지 않는 게시글입니다."));
    }

    private Post findPostByIdAndUserId(Long postId, Long userId) {
        return postRepository.findByIdAndUserId(postId, userId).orElseThrow(() -> new AuthorizationException());
    }
}
