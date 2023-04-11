package com.example.yobee.review.domain;

import com.example.yobee.user.domain.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "review_like")
public class ReviewLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewLikeId;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name="review_id")
    @JsonManagedReference
    private Review review;


//
//    @JsonBackReference//, orphanRemoval = true
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Gifticon> gifticonList;
//
//    @JsonBackReference
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Favorites> favoriteskList;

    @Builder
    public ReviewLike(Long reviewLikeId) {
        this.reviewLikeId = reviewLikeId;


    }



}
