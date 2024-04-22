package com.server.nodak.domain.post.domain;

import com.server.nodak.domain.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "category")
@SQLDelete(sql = "UPDATE category SET is_deleted = true WHERE id = ?")
public class Category extends BaseEntity {

    @NotNull
    private String title;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    @ColumnDefault("false")
    private boolean isDeleted;

    public Category(String title) {
        this.title = title;
    }

    public void addPost(Post post) {
        this.posts.add(post);
    }

    public void deletePost(Post post) {
        this.posts.remove(post);
    }
}
