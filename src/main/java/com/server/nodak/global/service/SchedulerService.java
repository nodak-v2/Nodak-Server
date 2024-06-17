package com.server.nodak.global.service;

import com.server.nodak.domain.user.repository.UserHistoryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final UserHistoryRepository userHistoryRepository;

    @Scheduled(cron = "0 5 0 * * *")
    public void findByUser() {
        // 오늘 15일
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).minusDays(29);

        userHistoryRepository.deleteAllByActionDateTimeBefore(localDateTime);
    }
}
