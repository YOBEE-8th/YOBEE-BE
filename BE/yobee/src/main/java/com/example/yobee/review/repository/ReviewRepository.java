package com.example.yobee.review.repository;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.dto.MyReivewDto;
import com.example.yobee.review.domain.Review;
import com.example.yobee.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByrecipeAndContentNotNullOrderByCreatedAt(Recipe recipe);

    List<Review> findByUser(User user);

    List<Review> findByUserAndRecipeOrderByCreatedAt(User user, Recipe recipe);

    Review findByreviewImage(String reviewImage);

    Long countByrecipeAndContentNotNull(Recipe recipe);
}
