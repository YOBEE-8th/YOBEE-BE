package com.example.yobee.user.domain;

import com.example.yobee.recipe.domain.RecipeLike;
import com.example.yobee.review.domain.Review;
import com.example.yobee.review.domain.ReviewLike;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;
    private String email;
    private String password;

    private int type;

    private String fcmToken;

    private String refreshToken;

    private String nickName;

    private String profileImage;

    private int level;



    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)    // 1:1 대상 테이블에 외래 키 - 양방향
    private Experience experience;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviewList;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ReviewLike> reviewLikeList;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RecipeLike> recipeLikeList;

    @Builder
    public User(Long userId,
                String email,
                String password,
                int type,
                String fcmToken,
                String refreshToken,
                String nickName,
                String profileImage,
                int level) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.type = type;
        this.fcmToken =  fcmToken;
        this.refreshToken = refreshToken;
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.level = level;

    }



}
