package com.server.nodak.domain.vote.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.post.domain.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Entity
@NoArgsConstructor
@ToString(of = {"title"})
public class Vote extends BaseEntity {

    @Column(nullable = false)
    @NotBlank
    private String title;

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @OneToMany(mappedBy = "vote", cascade = {CascadeType.PERSIST,
            CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VoteOption> voteOptions = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

    @Setter
    private boolean isTerminated = false;

    @Builder
    public Vote(String title, Post post, List<VoteOption> voteOptions, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        setPost(post);
        this.voteOptions = voteOptions != null ? voteOptions : new ArrayList<>();
        this.startDate = startDate;
        this.endDate = endDate;
        this.isTerminated = false;
    }

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.setVote(null);
        }
        this.post = post;
        this.post.setVote(this);
    }

    public void removeVoteOption(VoteOption voteOption) {
        this.voteOptions.remove(voteOption);
    }

    public void addVoteOption(VoteOption voteOption) {
        this.voteOptions.add(voteOption);
    }
}
