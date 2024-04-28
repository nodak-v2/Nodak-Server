package com.server.nodak.domain.vote.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.post.domain.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @OneToMany(mappedBy = "vote", cascade = {CascadeType.PERSIST,
            CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VoteOption> voteOptions;

    @Builder
    public Vote(String title, Post post) {
        this.title = title;
        this.voteOptions = new ArrayList<>();
        setPost(post);
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void removeVoteOption(VoteOption voteOption) {
        this.voteOptions.remove(voteOption);
    }

    public void addVoteOption(VoteOption voteOption) {
        this.voteOptions.add(voteOption);
    }
}
