package com.server.nodak.domain.post.domain;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.post.dto.PostRequest;
import com.server.nodak.domain.post.dto.PostSearchResponse;
import com.server.nodak.domain.user.domain.User;
import com.server.nodak.domain.vote.domain.Vote;
import com.server.nodak.domain.vote.domain.VoteOption;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
@SQLRestriction("is_deleted = false")
public class Post extends BaseEntity {

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private final List<StarPost> starPosts = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true)
    private final List<Comment> comments = new ArrayList<>();
    @NotBlank
    private String content;
    private int stars;
    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    private boolean isDeleted;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false, name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;
    @OneToOne(mappedBy = "post", cascade = {CascadeType.PERSIST,
        CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private Vote vote;

    @Builder
    public Post(String content, User user, Category category, Vote vote) {
        this.content = content;
        this.setUser(user);
        this.setCategory(category);
        this.setVote(vote);
    }

    private void setCategory(Category category) {
        if (this.category != null) {
            this.category.getPosts().remove(this);
        }
        this.category = category;
        this.category.getPosts().add(this);
    }

    private void setUser(User user) {
        this.user = user;
        user.getPosts().add(this);
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public void delete(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void update(PostRequest request) {
        if (!request.getContent().equals(content)) {
            this.content = request.getContent();
        }
    }

    public void addVoteComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    public PostSearchResponse toSearchResponse() {
        long voterCount = 0;

        for (VoteOption voteOption : this.vote.getVoteOptions()) {
            voterCount += voteOption.getVoteHistories().size();
        }

        return PostSearchResponse.builder()
            .postId(this.getId())
            .voteId(this.vote.getId())
            .voteTitle(this.vote.getTitle())
            .commentCount(this.comments.size())
            .likeCount(this.starPosts.size())
            .voterCount(voterCount)
            .author(this.user.getNickname())
            .profileImageUrl(this.user.getProfileImageUrl())
            .createdAt(this.getCreatedAt())
            .build();
    }
}
