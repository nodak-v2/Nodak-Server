package com.server.nodak.domain.vote.perform;

import static com.server.nodak.domain.vote.domain.QVoteHistory.voteHistory;
import static com.server.nodak.domain.vote.domain.QVoteOption.voteOption;
import static com.server.nodak.domain.vote.utils.Utils.createPost;
import static com.server.nodak.domain.vote.utils.Utils.createVote;
import static com.server.nodak.domain.vote.utils.Utils.createVoteHistory;
import static com.server.nodak.domain.vote.utils.Utils.createVoteOption;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.nodak.domain.post.domain.Category;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.repository.CategoryRepository;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserProvider;
import com.server.nodak.domain.user.repository.UserJpaRepository;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteHistory;
import com.server.nodak.domain.vote.domain.VoteOption;
import com.server.nodak.domain.vote.repository.votehistory.VoteHistoryRepository;
import com.server.nodak.global.config.QueryDslConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@ActiveProfiles("perform")
@Import({QueryDslConfig.class})
@DisplayName("VoteHistory 인덱스 최적화 테스트")
@Slf4j
@Rollback(value = false)
@Transactional
public class VoteHistoryIndexOptimizeTest {

    Random rnd = new Random();

    @Autowired
    JPAQueryFactory queryFactory;

    @Autowired
    UserJpaRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    VoteHistoryRepository voteHistoryRepository;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    @Disabled
    @DisplayName("existsHistoryByVoteId 비용을 계산합니다.")
    public void existsHistoryByTest() {
        Long cost = Stream.generate(() -> {
                    Long userId = rnd.nextLong(1, 1000);
                    Long voteId = rnd.nextLong(1, 40000);

                    JPAQuery<Integer> query = queryFactory.selectOne()
                            .from(voteOption)
                            .innerJoin(voteHistory)
                            .on(voteOption.id.eq(voteHistory.voteOption.id))
                            .where(
                                    voteOption.vote.id.eq(voteId),
                                    voteHistory.user.id.eq(userId)
                            );
                    return fetchFirstQuery(query);
                })
                .limit(10)
                .reduce(0L, Long::sum);

        log.info("existsHistoryByVoteId time cost : {}", cost / 10);
    }

    @Test
    @Disabled
    @DisplayName("findVoteIdAndUserId 비용을 계산합니다.")
    public void findVoteIdAndUserId() {
        Long cost = Stream.generate(() -> {
                    Long userId = rnd.nextLong(1, 1000);
                    Long voteId = rnd.nextLong(1, 40000);

                    JPAQuery<VoteHistory> query = queryFactory
                            .selectFrom(voteHistory)
                            .where(voteHistory.user.id.eq(userId).and(voteHistory.voteOption.vote.id.eq(voteId))
                            );
                    return fetchFirstQuery(query);
                })
                .limit(10)
                .reduce(0L, Long::sum);

        log.info("findVoteIdAndUserId time cost : {}", cost / 10);
    }

    private long fetchFirstQuery(JPAQuery<?> query) {
        long startTime = System.currentTimeMillis();
        query.fetchFirst();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    @Test
    @Disabled
    public void createUserAndCategoryTestData() {
        saveUser(1000);
        saveCategory();
    }

    @Test
    @Transactional
    @Disabled
    public void createPostTestData() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<? extends Future<?>> futures = Stream.generate(
                () -> executorService.submit(() -> {
                    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) {
                            savePost(2000, 3, 10);
                        }
                    });
                })
        ).limit(20).toList();

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void saveUser(int idx) {
        userRepository.saveAll(createUsers(idx, UserProvider.KAKAO));
    }

    public void saveCategory() {
        List.of("운동", "연애", "개발", "요리", "잡담").stream().forEach(category -> {
            categoryRepository.save(new Category(category));
        });
    }

    public void savePost(int postIdx, int voteOptionIdx, int voteHistoryIdx) {
        List<VoteOption> voteOptions = new ArrayList<>();
        List<VoteHistory> voteHistories = new ArrayList<>();

        IntStream.rangeClosed(1, postIdx).forEach(e -> {
            User user = userRepository.findById(rnd.nextLong(1, 200)).get();
            Category category = categoryRepository.findById(rnd.nextLong(1, 4)).get();
            Post post = createPost(user, randomUUID(1, 10), randomUUID(1, 10), category);
            Vote vote = createVote(randomUUID(1, 10), post);
            voteOptions.addAll(createVoteOptions(vote, voteOptionIdx));
        });

        voteOptions.stream().forEach(voteOption -> {
            List<VoteHistory> list = IntStream.rangeClosed(1, voteHistoryIdx).mapToObj(e -> {
                User user = userRepository.findById(rnd.nextLong(1, 200)).get();
                return createVoteHistory(user, voteOption);
            }).toList();
            voteHistories.addAll(list);
        });

        voteHistoryRepository.saveAll(voteHistories);
    }

    private List<User> createUsers(int idx, UserProvider provider) {
        return IntStream.rangeClosed(1, idx).mapToObj(e ->
                User.createUser(
                        randomUUID(1, 10),
                        randomUUID(1, 10),
                        randomUUID(1, 10),
                        provider)
        ).toList();
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
