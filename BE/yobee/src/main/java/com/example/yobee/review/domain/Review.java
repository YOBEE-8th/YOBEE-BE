package com.example.yobee.review.domain;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.user.domain.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name="recipe_id")
    @JsonManagedReference
    private Recipe recipe;

    private String content;

    private String reviewImage;
    private int reviewLikeCnt;
    private Date createdAt;

    @JsonBackReference
    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
    private List<ReviewLike> reviewLikeList;

    @Builder
    public Review(Long reviewId,
                  String content,
                  String reviewImage,
                  int reviewLikeCnt,
                  Date createdAt
                  ) {
        this.reviewId = reviewId;
        this.content = content;
        this.reviewImage = reviewImage;
        this.reviewLikeCnt = reviewLikeCnt;
        this.createdAt = createdAt;
    }



}
