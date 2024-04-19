package com.server.nodak.domain.vote.domain;

import com.server.nodak.domain.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@NoArgsConstructor
@ToString(of = {"title", "seq"})
public class VoteOption extends BaseEntity {
    @Column(nullable = false)
    @NotBlank
    private String content;

    @Column(nullable = false)
    @Min(0)
    private int seq;

    @ManyToOne(cascade = {CascadeType.PERSIST}, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Vote vote;

    @OneToMany(mappedBy = "voteOption", cascade = {CascadeType.PERSIST,
            CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VoteHistory> voteHistories;


    @Builder
    public VoteOption(String content, int seq, Vote vote) {
        this.content = content;
        this.seq = seq;
        setVote(vote);
        this.voteHistories = new ArrayList<>();
    }

    public void setVote(Vote vote) {
        if (this.vote != null) {
            vote.removeVoteOption(this);
        }
        this.vote = vote;
        this.vote.addVoteOption(this);
    }

    public void removeVoteHistory(VoteHistory voteHistory) {
        this.voteHistories.remove(voteHistory);
    }

    public void addVoteHistory(VoteHistory voteHistory) {
        this.voteHistories.add(voteHistory);
    }
}
