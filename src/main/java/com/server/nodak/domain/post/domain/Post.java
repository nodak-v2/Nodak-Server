package com.server.nodak.domain.post.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
@SQLDelete(sql = "UPDATE post SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class Post extends BaseEntity {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String imageUrl;

    private int stars;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    private boolean isDeleted;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<StarPost> starPosts = new ArrayList<>();

    @Builder
    public Post(String title, String content, String imageUrl, User user, Category category) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.setUser(user);
        this.setCategory(category);
    }

    private void setCategory(Category category) {
        this.category = category;
        category.getPosts().add(this);
    }

    private void setUser(User user) {
        this.user = user;
        user.getPosts().add(this);
    }
}
