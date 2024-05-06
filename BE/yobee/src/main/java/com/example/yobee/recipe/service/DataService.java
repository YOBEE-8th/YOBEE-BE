package com.example.yobee.recipe.service;

import com.example.yobee.recipe.domain.HashTag;
import com.example.yobee.recipe.domain.Ingredient;
import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.domain.RecipeStep;
import com.example.yobee.recipe.dto.Recipe10000Dto;
import com.example.yobee.recipe.repository.HashTagRepository;
import com.example.yobee.recipe.repository.IngredientRepository;
import com.example.yobee.recipe.repository.RecipeRepository;
import com.example.yobee.recipe.repository.RecipeStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {
    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final HashTagRepository hashTagRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public void getRecipeFrom10000(Recipe10000Dto recipe10000Dto) {
        String name = recipe10000Dto.getName();
        String id = recipe10000Dto.getId();
        String mainImg = recipe10000Dto.getMain();
        String category = recipe10000Dto.getCategory();
        String hashTagList = recipe10000Dto.getHashTagList();
        String info = recipe10000Dto.getInfo();

        String serving = info.split(",")[0];
        String time = info.split(",")[1];
        String diff = info.split(",")[2];

        String[] ingredientList = recipe10000Dto.getIngredient();
        String[] step = recipe10000Dto.getStep();


        Recipe recipe = new Recipe();
        recipe.setRecipeTitle(name);
        recipe.setDifficulty(Integer.parseInt(diff));
        recipe.setServings(serving);
        recipe.setAi(false);
        recipe.setTime(time);
        recipe.setCategory(category);
        recipe.setResultImage(mainImg);
        recipe.setRecipe10000Id(Integer.parseInt(id));

        recipeRepository.save(recipe);
        Recipe target = new Recipe();
        if (recipeRepository.findByName(name).isPresent()) {
            target = recipeRepository.findByName(name).get();
        }
        for(String text : hashTagList.split("#")) {
            HashTag hashTag = new HashTag();
            hashTag.setRecipe(target);
            hashTag.setTag(text);

            hashTagRepository.save(hashTag);
        }
        for (String stepAndImg : step) {
            RecipeStep recipeStep = new RecipeStep();
            recipeStep.setTimer(0L);
            recipeStep.setFire(0);
            recipeStep.setTemperature(0);
            recipeStep.setStepDescription(stepAndImg.split("---")[0]);
            recipeStep.setStepImage(stepAndImg.split("---")[1]);
            recipeStep.setRecipe(target);
            recipeStepRepository.save(recipeStep);
        }

        for (String nameWeight : ingredientList) {
            Ingredient ingredient = new Ingredient();
            String[] temp = nameWeight.split("::");
            ingredient.setRecipe(target);
            ingredient.setIngredientName(temp[0]);
            if (temp.length > 1){
                ingredient.setWeight(temp[1]);
            }else{
                ingredient.setWeight("");
            }
            ingredientRepository.save(ingredient);
        }
        log.info("데이터 추가 {}",name);
    }
}
