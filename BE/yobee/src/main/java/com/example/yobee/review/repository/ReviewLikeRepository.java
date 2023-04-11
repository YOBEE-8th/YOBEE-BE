package com.example.yobee.review.repository;

import com.example.yobee.review.domain.Review;
import com.example.yobee.review.domain.ReviewLike;
import com.example.yobee.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    ReviewLike findByUserAndReview(User user, Review review);
    Boolean existsByUserAndReview(User user, Review review);
    List<ReviewLike> findByUser(User user);
}
