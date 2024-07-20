package com.server.nodak.global.service;

import com.server.nodak.domain.user.repository.UserHistoryRepository;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.repository.vote.VoteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {

    private final UserHistoryRepository userHistoryRepository;

    private final VoteRepository voteRepository;

    @Scheduled(cron = "0 5 0 * * *")
    public void findByUser() {
        // 오늘 15일
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0))
            .minusDays(29);

        userHistoryRepository.deleteAllByActionDateTimeBefore(localDateTime);
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void test() {
        LocalDateTime dateTime = LocalDateTime.now();
        List<Vote> terminatedVotes = voteRepository.findAllByIsTerminatedAndEndDateBefore(
            false, dateTime);
        terminatedVotes.stream().forEach(vote -> vote.setTerminated(true));

        voteRepository.saveAll(terminatedVotes);
    }
}
