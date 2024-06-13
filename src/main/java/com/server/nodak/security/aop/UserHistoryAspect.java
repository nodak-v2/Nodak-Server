package com.server.nodak.security.aop;

import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.user.domain.UserHistory;
import com.server.nodak.domain.user.repository.UserHistoryRepository;
import com.server.nodak.domain.user.repository.UserRepository;
import com.server.nodak.exception.common.DataNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class UserHistoryAspect {

    private final UserRepository userRepository;
    private final UserHistoryRepository userHistoryRepository;

    @Pointcut("@annotation(com.server.nodak.security.aop.IncreaseUserHistory)")
    public void increaseHistoryPointCut() {
    }

    @Around(value = "increaseHistoryPointCut() && args(userId, ..)")
    public void increaseHistoryExecute(ProceedingJoinPoint joinPoint, Long userId) throws Throwable {

        joinPoint.proceed();

        System.out.println("userId : " + userId);
        System.out.println("증가");

        // 오늘 날짜에 대한 기록이 없다면
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));

        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException());

        Optional<UserHistory> optionalUserHistory = userHistoryRepository.findByUserIdAndActionDateTime(userId,
                now);
        if (optionalUserHistory.isEmpty()) {
            UserHistory userHistory = UserHistory.builder()
                    .actionDateTime(now)
                    .count(1L)
                    .user(user)
                    .build();
            userHistoryRepository.save(userHistory);
        } else {
            UserHistory userHistory = optionalUserHistory.get();
            userHistory.increaseCount();
            userHistoryRepository.save(userHistory);
        }
    }
}
