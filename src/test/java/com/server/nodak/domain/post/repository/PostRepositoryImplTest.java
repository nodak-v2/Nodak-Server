package com.server.nodak.domain.post.repository;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;
import static com.server.nodak.domain.vote.utils.Utils.createVoteHistory;
import static com.server.nodak.domain.vote.utils.Utils.createVoteOption;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.dto.PostResponse;
import com.server.nodak.domain.post.dto.PostSearchRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.domain.vote.repository.VoteHistoryRepository;
import com.server.nodak.domain.vote.repository.VoteOptionRepository;
import com.server.nodak.domain.vote.repository.VoteRepository;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("PostRepositoryImpl 테스트")
@Slf4j
class PostRepositoryImplTest {

    @Autowired
    EntityManager em;
    @Autowired
    PostRepository postRepository;
    @Autowired
    VoteRepository voteRepository;
    @Autowired
    VoteOptionRepository voteOptionRepository;
    @Autowired
    VoteHistoryRepository voteHistoryRepository;
    PageRequest pageRequest;
    User user;
    Category category1;
    Category category2;
    List<Post> posts = new ArrayList<>();
    List<VoteOption> voteOptions = new ArrayList<>();
    List<VoteHistory> voteHistories = new ArrayList<>();
    Random rnd = new Random();

    @BeforeEach
    public void setUp() {
        pageRequest = PageRequest.of(0, 10);
        user = createUser();
        category1 = createCategory("운동");
        category2 = createCategory("연애");
        em.persist(user);
        em.persist(category1);
        em.persist(category2);
    }

    @Test
    @DisplayName("findOne 테스트 - 게시글 상세보기")
    public void findOne() {
        // Given
        int postCount = rnd.nextInt(3) + 1;
        int voteOptionCount = rnd.nextInt(2, 5);
        int voteHistoryCount = rnd.nextInt(2, 5);
        saveVoteAndVoteOptions(postCount, voteOptionCount, voteHistoryCount);
        int selectId = rnd.nextInt(postCount);

        // When
        PostResponse response = postRepository.findOne(user.getId(), posts.get(selectId).getId()).get();

        // Then
        Assertions.assertThat(response.getTitle()).isEqualTo(posts.get(selectId).getTitle());
        Assertions.assertThat(response.getAuthor()).isEqualTo(posts.get(selectId).getUser().getNickname());
        Assertions.assertThat(response.getContent()).isEqualTo(posts.get(selectId).getContent());
        Assertions.assertThat(response.getImageUrl()).isEqualTo(posts.get(selectId).getImageUrl());
        Assertions.assertThat(response.getStarCount()).isEqualTo(posts.get(selectId).getStarPosts().size());
        Assertions.assertThat(response.getCheckStar()).isEqualTo(posts.get(selectId).getStarPosts().stream()
                .filter(e -> e.getUser().getNickname().equals(response.getAuthor())
                        && e.getPost().getId() == selectId).toList().size() > 0);
    }

    @Test
    @DisplayName("search 테스트 - 키워드 검색")
    void searchByTitleAndContent() {
        // Given
        String keyword = randomUUID(1, 2);
        PostSearchRequest searchRequest = PostSearchRequest.builder().keyword(keyword).build();
        int postCount = 10;
        int voteOptionCount = rnd.nextInt(1, 5);
        int voteHistoryCount = rnd.nextInt(1, 5);
        saveVoteAndVoteOptions(postCount, voteOptionCount, voteHistoryCount);
        long findPostCount = posts.stream()
                .filter(e -> e.getTitle().contains(keyword) || e.getContent().contains(keyword)).count();

        // Then
        Page<PostSearchResponse> result = postRepository.search(searchRequest, pageRequest);

        // When
        Assertions.assertThat(result.getContent().size()).isEqualTo(findPostCount);
        Assertions.assertThat(result.getSize()).isEqualTo(pageRequest.getPageSize());
    }

    @Test
    @DisplayName("search 테스트 - 카테고리 검색")
    public void searchByCategoryId() {
        // Given
        List<Post> posts = List.of(
                createPost(user, randomUUID(1, 10), randomUUID(1, 10), category1),
                createPost(user, randomUUID(1, 10), randomUUID(1, 10), category1),
                createPost(user, randomUUID(1, 10), randomUUID(1, 10), category2)
        );
        List<Post> savePosts = postRepository.saveAll(posts);
        PostSearchRequest searchRequest = PostSearchRequest.builder().categoryId(category1.getId()).build();
        List<Long> postIds = savePosts.stream().filter(post -> post.getCategory().getId() == category1.getId())
                .map(post -> post.getId()).toList();

        // When
        Page<PostSearchResponse> postsFromCategory1 = postRepository.search(searchRequest,
                pageRequest);

        // Then
        Assertions.assertThat(postsFromCategory1.getTotalElements()).isEqualTo(postIds.size());
    }

    private void saveVoteAndVoteOptions(int postCount, int voteOptionCount, int voteHistoryCount) {
        String uuid = randomUUID(1, 4);

        IntStream.range(0, postCount).forEach(cnt -> {
            Post post = createPost(user, uuid, uuid, category1);
            posts.add(post);
            Vote vote = createVote(String.format("Vote_title_%s", uuid), post);
            voteOptions.addAll(createVoteOptions(vote, voteOptionCount));
        });

        voteOptions.stream().forEach(voteOption -> {
            List<VoteHistory> historyList = IntStream.rangeClosed(1, voteHistoryCount).mapToObj(e -> {
                if (e == 1) {
                    return createVoteHistory(user, voteOption);
                }
                User tmpUser = createUser();
                em.persist(tmpUser);
                return createVoteHistory(tmpUser, voteOption);
            }).toList();
            voteHistories.addAll(historyList);
        });

        voteHistoryRepository.saveAll(voteHistories);
    }

    private List<VoteOption> createVoteOptions(Vote vote, int size) {
        return IntStream.rangeClosed(1, size).mapToObj(e ->
                createVoteOption(vote, e, String.format("VoteOption_content_%d", e))
        ).collect(Collectors.toList());
    }

    public String randomUUID(int start, int end) {
        return UUID.randomUUID().toString().substring(start, end);
    }
}