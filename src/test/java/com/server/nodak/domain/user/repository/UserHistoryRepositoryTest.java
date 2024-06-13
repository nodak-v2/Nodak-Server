package com.server.nodak.domain.user.repository;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserHistory;
import com.server.nodak.domain.vote.utils.Utils;
import com.server.nodak.global.config.QueryDslConfig;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class})
@DisplayName("UserHistoryRepositoryImpl 테스트")
@Slf4j
@Transactional
class UserHistoryRepositoryTest {

    @Autowired
    UserHistoryRepository userHistoryRepository;

    @Autowired
    UserJpaRepository userRepository;

    Random random = new Random();

    User user;

    @BeforeEach
    public void setUp() {
        user = Utils.createUser();
        userRepository.save(user);
    }

    @Test
    @DisplayName("특정 회원에 대한 날짜 사이의 데이터를 조회합니다.")
    public void findByActionDateTimeBetweenAndUserIdOrderByActionDateTime() {
        // given
        List<UserHistory> userHistories = generateRandomUserHistoryData(20);
        LocalDateTime startAt = LocalDateTime.of(2024, 6, 5, 0, 0);
        LocalDateTime endAt = LocalDateTime.of(2024, 6, 15, 23, 59);
        List<LocalDate> filterLocalDate = userHistories.stream().map(e -> e.getActionDateTime().toLocalDate()).toList()
                .stream().filter(e -> !e.isBefore(startAt.toLocalDate()) && !e.isAfter(endAt.toLocalDate())).toList();
        // when
        List<UserHistory> findUserHistories = userHistoryRepository.findByActionDateTimeBetweenAndUserIdOrderByActionDateTime(
                startAt, endAt, user.getId());
        // then
        Assertions.assertThat(findUserHistories.size()).isEqualTo(filterLocalDate.size());
    }


    public List<UserHistory> generateRandomUserHistoryData(int index) {
        Set<LocalDate> dates = new HashSet<>();

        List<UserHistory> userHistories = IntStream.rangeClosed(1, index).mapToObj(i -> {
            LocalDateTime dateTime;
            LocalDate date;
            do {
                dateTime = getRandomDate();
                date = dateTime.toLocalDate();
            } while (dates.contains(date));
            dates.add(date);
            User findUser = userRepository.findById(user.getId()).get();
            UserHistory userHistory = UserHistory.builder()
                    .user(findUser)
                    .count(random.nextLong(1, 15))
                    .actionDateTime(dateTime)
                    .build();
            return userHistoryRepository.save(userHistory);
        }).toList();

        return userHistories;
    }

    private LocalDateTime getRandomDate() {
        Random random = new Random();
        int day = random.nextInt(30) + 1;  // 1 to 30
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        int second = random.nextInt(60);
        return LocalDateTime.of(2024, Month.JUNE, day, hour, minute, second);
    }

}