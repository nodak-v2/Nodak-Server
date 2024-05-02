package com.server.nodak.domain.post.repository;

import static com.server.nodak.domain.vote.utils.Utils.createCategory;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createUser;
import static com.server.nodak.domain.vote.utils.Utils.createVote;
import static com.server.nodak.domain.vote.utils.Utils.createVoteHistory;
import static com.server.nodak.domain.vote.utils.Utils.createVoteOption;

import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
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
    User user;
    Category category;
    List<Post> posts = new ArrayList<>();
    Vote vote;
    List<VoteOption> voteOptions = new ArrayList<>();
    Random rnd = new Random();

    @BeforeEach
    public void setUp() {
        user = createUser();
        category = createCategory();
        em.persist(user);
        em.persist(category);
    }

    @Test
    @DisplayName("search 테스트 - 키워드 검색")
    void searchByTitleAndContent() {
        // Given
        String keyword = randomUUID(1, 5);
        PageRequest pageRequest = PageRequest.of(0, 10);
        PostSearchRequest searchRequest = PostSearchRequest.builder().keyword(keyword).build();
        int postCount = rnd.nextInt(1, 10);
        int voteOptionCount = rnd.nextInt(1, 5);
        int voteHistoryCount = rnd.nextInt(1, 5);
        IntStream.rangeClosed(1, postCount).forEach(e -> saveVoteAndVoteOptions(voteOptionCount, voteHistoryCount));

        // Then
        Page<PostSearchResponse> result = postRepository.search(searchRequest, pageRequest);

        // When
        long findPostCount = posts.stream()
                .filter(e -> e.getTitle().contains(keyword) || e.getContent().contains(keyword)).count();
        Assertions.assertThat(result.getContent().size()).isEqualTo(findPostCount);
        result.getContent().stream()
                .forEach(e -> Assertions.assertThat(e.getTotalCount()).isEqualTo(voteHistoryCount * voteOptionCount));
    }

    @Test
    @DisplayName("search 테스트 - 카테고리 검색")
    public void searchByCategoryId() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Category category1 = createCategory("운동");
        Category category2 = createCategory("연애");
        List<Post> posts = List.of(
                createPost(user, String.format("title_%s", randomUUID(1, 10)),
                        String.format("content_%s", randomUUID(1, 10)),
                        category1),
                createPost(user, String.format("title_%s", randomUUID(1, 10)),
                        String.format("content_%s", randomUUID(1, 10)),
                        category1),
                createPost(user, String.format("title_%s", randomUUID(1, 10)),
                        String.format("content_%s", randomUUID(1, 10)),
                        category2)
        );
        List<Post> savePosts = postRepository.saveAll(posts);
        PostSearchRequest searchRequest = PostSearchRequest.builder().categoryId(category1.getId()).build();
        List<Long> postIds = savePosts.stream().filter(post -> post.getCategory().getId() == category1.getId())
                .map(post -> post.getId()).toList();

        // When

        Page<PostSearchResponse> postsFromCategory1 = postRepository.search(searchRequest,
                pageRequest);

        // Then
        postsFromCategory1.getContent().stream().forEach(res -> {
            Assertions.assertThat(res.getPostId()).isIn(postIds);
        });
//        Assertions.assertThat(postsFromCategory1.getSize()).isEqualTo(pageRequest.getPageSize());
    }

    private void saveVoteAndVoteOptions(int voteOptionCount, int voteHistoryCount) {
        String uuid = randomUUID(1, 10);
        Post post = createPost(user, String.format("Post_title_%s", uuid), String.format("Post_content_%s", uuid),
                category);
        posts.add(post);
        vote = createVote(String.format("Vote_title_%s", uuid), post);
        voteOptions = createVoteOptions(vote, voteOptionCount);
        voteOptions.stream().forEach(voteOption -> {
            voteHistoryRepository.saveAll(createVoteHistorys(user, voteOption, voteHistoryCount));
        });

    }

    private List<VoteHistory> createVoteHistorys(User user, VoteOption voteOption, int size) {
        return IntStream.rangeClosed(1, size).mapToObj(e ->
                createVoteHistory(user, voteOption)
        ).collect(Collectors.toList());
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