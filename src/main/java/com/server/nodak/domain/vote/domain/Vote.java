package com.server.nodak.domain.vote.domain;

import com.server.nodak.domain.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
@ToString(of = {"title"})
public class Vote extends BaseEntity {
    
    @Column(nullable = false)
    @NotBlank
    private String title;

    // TODO : POST 필드 추가 예정

    @OneToMany(mappedBy = "vote", cascade = {CascadeType.PERSIST,
            CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VoteOption> voteOptions;

    @Builder
    public Vote(String title) {
        this.title = title;
        this.voteOptions = new ArrayList<>();
    }

    public void removeVoteOption(VoteOption voteOption) {
        this.voteOptions.remove(voteOption);
    }

    public void addVoteOption(VoteOption voteOption) {
        this.voteOptions.add(voteOption);
    }
}
