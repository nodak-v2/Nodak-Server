package com.server.nodak.domain.user.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.user.dto.UserHistoryListResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserHistory extends BaseEntity {

    private Long count;

    private LocalDateTime actionDateTime;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    public UserHistory(Long count, User user, LocalDateTime actionDateTime) {
        this.count = count;
        setUser(user);
        this.actionDateTime = actionDateTime;
    }

    private void setUser(User user) {
        this.user = user;
    }

    public UserHistoryListResponse toListResponse() {
        return UserHistoryListResponse.builder()
                .date(this.actionDateTime.toLocalDate())
                .level(checkLevel())
                .build();
    }

    private Long checkLevel() {
        if (this.count >= 6) {
            return 3L;
        } else if (this.count >= 3) {
            return 2L;
        }
        return 1L;
    }

    public void increaseCount() {
        this.count++;
    }
}
