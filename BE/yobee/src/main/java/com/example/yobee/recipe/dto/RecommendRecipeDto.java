package com.example.yobee.recipe.dto;

import com.example.yobee.recipe.domain.Recipe;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendRecipeDto {
    @ApiModelProperty(value = "recipeId", notes = "레시피id", example = "2")
    private Long recipeId;
    @ApiModelProperty(value = "image", notes = "레시피 완성 이미지", example = "하와이안피자.jpg")
    private String image;
    @ApiModelProperty(value = "title", notes = "레시피 제목", example = "파인애플 피자")
    private String title;


    public RecommendRecipeDto(Recipe recipe) {
        this.recipeId = recipe.getRecipeId();
        this.image = recipe.getResultImage();
        this.title = recipe.getRecipeTitle();
    }
}
