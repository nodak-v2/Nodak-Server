package com.server.nodak.global.service;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserHistory;
import com.server.nodak.domain.user.domain.UserProvider;
import com.server.nodak.domain.user.repository.UserHistoryRepository;
import com.server.nodak.domain.user.repository.UserJpaRepository;
import com.server.nodak.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("SchedulerService 테스트")
@Transactional
@Slf4j
class SchedulerServiceTest {

    @Autowired
    UserJpaRepository userRepository;

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    EntityManager em;

    User user;

    @Test
    @DisplayName("deleteAllByActionDateTime 성능 테스트")
    @Disabled
    @Rollback(false)
    public void deleteAllByActionDateTime() {
        // given
        saveUserHistory();
        em.clear();

        // when
        long startTime = System.currentTimeMillis();
        userHistoryRepository.deleteAllByActionDateTimeBefore(LocalDateTime.now());
        long endTime = System.currentTimeMillis();

        // then
        log.info("deleteAllByActionDateTimeBefore() time cost : {}", endTime - startTime);
    }

    @Test
    @DisplayName("deleteAllByIdInBatch 성능 테스트")
    @Disabled
    @Rollback(false)
    public void deleteAllByIdInBatch() {
        // given
        saveUserHistory();
        em.clear();

        // when
        long startTime = System.currentTimeMillis();
        List<UserHistory> toRemoveData = userHistoryRepository.findByActionDateTimeBefore(
                LocalDateTime.now());
        List<Long> ids = toRemoveData.stream().map(data -> data.getId()).toList();

        userHistoryRepository.deleteAllByIdInBatch(ids);

        long endTime = System.currentTimeMillis();

        //then
        log.info("deleteAllByIdInBatch() time cost : {}", endTime - startTime);
    }

    @Test
    @DisplayName("deleteAllById 성능 테스트")
    @Disabled
    @Rollback(false)
    public void deleteAllById() {
        // given
        saveUserHistory();
        em.clear();

        // when
        long startTime = System.currentTimeMillis();

        List<UserHistory> toRemoveData = userHistoryRepository.findByActionDateTimeBefore(
                LocalDateTime.now());
        List<Long> ids = toRemoveData.stream().map(data -> data.getId()).toList();

        userHistoryRepository.deleteAllById(ids);

        long endTime = System.currentTimeMillis();

        //then
        log.info("deleteAllById() time cost : {}", endTime - startTime);
    }

    // 생성 데이터 개수 : limit(10) * saveUserHistory 인자 값(1000) * 반복문 횟수(10) = 10만건
    @Test
    @Disabled
    @Rollback(false)
    @DisplayName("대량의 사용자 기록 데이터를 저장합니다.")
    public void saveUserHistory() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 1; i++) {
            List<? extends Future<?>> futures = Stream.generate(
                    () -> executorService.submit(() -> {
                        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                            @Override
                            protected void doInTransactionWithoutResult(TransactionStatus status) {
                                saveUserHistory(100);
                            }
                        });
                    })
            ).limit(10).toList();

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
    }

    @Test
    @Disabled
    @Rollback(false)
    @DisplayName("사용자 기록 데이터 생성을 위한 테스트 유저를 저장합니다.")
    public void saveUser() {
        User user = User.createUser("테스트 유저", "1234", "테스트 닉네임", UserProvider.KAKAO);

        userRepository.save(user);
    }

    public void saveUserHistory(int index) {
        User user = userRepository.findByEmail("테스트 유저").get();

        List<UserHistory> userHistories = IntStream.rangeClosed(1, index).mapToObj(i ->
                UserHistory.builder()
                        .count(0L)
                        .user(user)
                        .actionDateTime(LocalDateTime.now())
                        .build()).toList();

        userHistoryRepository.saveAll(userHistories);
    }
}