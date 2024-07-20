package com.server.nodak.domain.reply.entity;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.post.domain.Post;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "reply")
public class Reply extends BaseEntity {

    @NotBlank
    @Setter
    private String content;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    @Setter
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Comment comment;

    @Builder
    public Reply(String content, User user, Comment comment) {
        this.content = content;
        this.user = user;
        this.comment = comment;
    }
}
