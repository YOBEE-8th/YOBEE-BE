package com.example.yobee.configuration;

import com.example.yobee.recipe.repository.JpaRecipeRepository;
import com.example.yobee.recipe.repository.RecipeLikeRepository;
import com.example.yobee.recipe.service.RecipeService;
import com.example.yobee.review.repository.ReviewRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class RecipeConfig {

    private EntityManager em;

    public RecipeConfig(EntityManager em) {
        this.em = em;
    }


    @Bean
    public JpaRecipeRepository jpaRecipeRepository() {
    return new JpaRecipeRepository(em);
    }

}
