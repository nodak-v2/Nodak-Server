package com.server.nodak.domain.post.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "star_post")
@SQLRestriction("is_deleted = false")
public class StarPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    private boolean isDeleted;

    @Builder
    public StarPost(User user, Post post) {
        setUser(user);
        setPost(post);
    }

    private void setPost(Post post) {
        if (this.post != null) {
            this.post.getStarPosts().remove(this);
        }
        this.post = post;
        this.post.getStarPosts().add(this);
    }

    private void setUser(User user) {
        this.user = user;
    }

    public void delete(boolean isDeleted) {
        this.isDeleted = isDeleted ? true : false;
    }
}
