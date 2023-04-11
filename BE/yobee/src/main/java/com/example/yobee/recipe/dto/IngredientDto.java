package com.example.yobee.recipe.dto;

import com.example.yobee.recipe.domain.Ingredient;
import com.example.yobee.recipe.domain.Recipe;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class IngredientDto {
    @ApiModelProperty(value = "ingredient", notes = "재료", example = "[돼지고기 40g, 고추장 10g, 대파 2개]")
    private List<String> ingredient;

    public IngredientDto(Recipe recipe) {
        List<String> temp2 = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredientList()){
            temp2.add(ingredient.getIngredientName()+" "+ingredient.getWeight());
        }
        this.ingredient = temp2;
    }
}
