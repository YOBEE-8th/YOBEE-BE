package com.example.yobee.recipe.repository;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.dto.PageSearchDto;
import com.example.yobee.recipe.dto.PageSortDto;
import com.example.yobee.recipe.dto.SortDto;
import com.example.yobee.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    List<Recipe> sortRecipe(SortDto sortDto);

    Recipe findById(Long id);

    void likeIncrease(Long id);

    void likeDecrease(Long id);

    List<Recipe> findAll();

    List<Recipe> likeRecipe(Long id);

    Optional<Recipe> findByName(String name);

    Recipe save(Recipe recipe);

    void deleteRecipeById(Long id);

    void deleteRecipeByName(String name);

    List<Recipe> searchRecipe(SortDto sortDto, String keyword);

    List<Recipe> pagenationSortRecipe(PageSortDto pageSortDto);

    List<Recipe> pagenationSearchRecipe(PageSearchDto pageSearchDto);
}
