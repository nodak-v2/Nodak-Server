package com.server.nodak.domain.post.domain;

import com.server.nodak.domain.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "category")
@SQLDelete(sql = "UPDATE category SET is_deleted = true WHERE id = ?")
@SQLRestriction("is_deleted = false")
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
}
