package com.server.nodak.domain.comment.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.post.domain.StarPost;
import com.server.nodak.domain.reply.entity.Reply;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends BaseEntity {

    @NotBlank
    @Setter
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @Setter
    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    private boolean isDeleted;

    @OneToMany(mappedBy = "comment", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Builder
    public Comment(String content, User user, Post post) {
        this.content = content;
        this.user = user;
        setPost(post);
    }

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.removeComment(this);
        }
        this.post = post;
        this.post.addVoteComment(this);
    }
}
