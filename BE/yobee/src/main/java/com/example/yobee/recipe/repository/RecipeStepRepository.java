package com.example.yobee.recipe.repository;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.domain.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    List<RecipeStep> findByRecipeOrderByRecipeStepIdAsc(Recipe recipe);
}
