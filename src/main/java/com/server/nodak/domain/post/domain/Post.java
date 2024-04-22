package com.server.nodak.domain.post.domain;

import com.server.nodak.domain.comment.domain.Comment;
import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
@SQLDelete(sql = "UPDATE post SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted is false")
public class Post extends BaseEntity {

    @NotNull
    private String title;

    @NotNull
    private String content;

    private String imageUrl;

    private int stars;

    private int views;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<StarPost> starPosts = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String content, String imageUrl, int stars, int views, User user) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.stars = stars;
        this.views = views;
        this.user = user;
    }

    public void addStarPost(StarPost starPost) {
        this.starPosts.add(starPost);
    }

    public void deleteStartPost(StarPost starPost) {
        this.starPosts.remove(starPost);
    }
}
