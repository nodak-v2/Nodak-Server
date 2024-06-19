package com.server.nodak.domain.post.service;

import com.server.nodak.domain.notification.controller.NotificationController;
import com.server.nodak.domain.notification.service.NotificationService;
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
import com.server.nodak.domain.user.repository.UserHistoryRepository;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.exception.common.AuthorizationException;
import com.server.nodak.exception.common.BadRequestException;
import com.server.nodak.exception.common.ConflictException;
import com.server.nodak.exception.common.DataNotFoundException;
import com.server.nodak.security.aop.IncreaseUserHistory;
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

    private final NotificationController notificationController;
    private final NotificationService notificationService;

    private final UserHistoryRepository userHistoryRepository;

    @Transactional
    @IncreaseUserHistory(incrementValue = 2)
    public void savePost(Long userId, PostRequest request) {
        User user = findUserById(userId);
        Category category = findCategoryByTitle(request.getChannel());

        Post post = createPost(user, category, request);
        Vote vote = createVote(post, request);

        request.getVoteOptionContent().entrySet().stream()
                .map(e -> createVoteOption(e.getKey(), e.getValue(), vote))
                .toList();

        postRepository.save(post);

        notificationService.saveNotificationToRedis(post.getId(), user.getNickname() + "님이 새 게시글을 작성했습니다.", user.getId());
        notificationService.notifyFollowersBySse(user, post);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchResponse> findPostBySearch(PostSearchRequest request, Pageable pageable) {
        return postRepository.search(request, pageable);
    }

    @Transactional(readOnly = true)
    public PostResponse findPost(Long userId, Long postId) {
        return postRepository.findOne(userId, postId).orElseThrow(() -> new DataNotFoundException());
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
    @IncreaseUserHistory
    public void registerLike(Long userId, Long postId) {
        if (starPostRepository.findByUserIdAndPostId(userId, postId).isEmpty()) {
            StarPost starPost = createStarPost(findUserById(userId), findPostById(postId));
            starPostRepository.save(starPost);
            return;
        }
        throw new ConflictException();
    }

    @Transactional
    public void cancleLike(Long userId, Long postId) {
        StarPost starPost = starPostRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new BadRequestException());
        starPost.delete(true);
        starPostRepository.save(starPost);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchResponse> findMyPosting(Long userId, Pageable pageable) {
        return postRepository.findMyPosting(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchResponse> findMyVoteHistory(Long userId, Pageable pageable) {
        return postRepository.findMyVoteHistory(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchResponse> findMyComment(Long userId, Pageable pageable) {
        return postRepository.findMyComment(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchResponse> findMyLike(Long userId, Pageable pageable) {
        return postRepository.findMyLike(userId, pageable);
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

    private Vote createVote(Post post, PostRequest postRequest) {
        return Vote.builder()
                .title(postRequest.getTitle())
                .startDate(postRequest.getStartDate())
                .endDate(postRequest.getEndDate())
                .post(post)
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

    @Transactional
    public void terminateVote(long userId, Long postId) {
        Post post = findPostByIdAndUserId(postId, userId);
        Vote vote = post.getVote();

        if (vote.isTerminated() == true){
            throw new BadRequestException("vote has already terminated.");
        }
        vote.setTerminated(true);
    }
}
