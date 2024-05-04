package com.server.nodak.domain.vote.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class VoteHistory extends BaseEntity {

    @ManyToOne(cascade = {CascadeType.PERSIST}, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private VoteOption voteOption;

    @ManyToOne(cascade = {CascadeType.PERSIST}, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    public VoteHistory(VoteOption voteOption, User user) {
        setVoteOption(voteOption);
        this.user = user;
    }

    private void setVoteOption(@NotBlank VoteOption voteOption) {
        if (this.voteOption != null) {
            this.voteOption.removeVoteHistory(this);
        }
        this.voteOption = voteOption;
        this.voteOption.addVoteHistory(this);
    }
}
