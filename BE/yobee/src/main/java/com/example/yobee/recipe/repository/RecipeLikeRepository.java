package com.example.yobee.recipe.repository;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.domain.RecipeLike;
import com.example.yobee.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, Long> {
    RecipeLike findByUserAndRecipe(User user, Recipe recipe);
    Boolean existsByUserAndRecipe(User user, Recipe recipe);
    List<RecipeLike> findByUser(User user);
}
