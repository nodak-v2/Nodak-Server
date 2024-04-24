package com.server.nodak.domain.user.domain;

import com.server.nodak.domain.model.BaseEntity;
import com.server.nodak.domain.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

import static com.server.nodak.domain.user.domain.UserRole.GENERAL;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
@SQLRestriction("is_deleted is null")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String profileImageUrl;

    private Boolean isDeleted;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Post> posts = new ArrayList<>();


    private User(String email, String password, String nickname, UserProvider provider) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.role = GENERAL;
    }

    public static User createUser(String email, String password, String nickname, UserProvider provider) {
        return new User(email, password, nickname, provider);
    }

    public void updateImage(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void updateRole(UserRole role) {
        this.role = role;
    }

    public void delete(boolean isDeleted) {
        this.isDeleted = isDeleted ? true : null;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
