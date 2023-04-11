package com.example.yobee.recipe.dto;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.domain.RecipeLike;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipesDto {
    @ApiModelProperty(value = "recipeId", notes = "레시피id", example = "1")
    private Long recipeId;
    @ApiModelProperty(value = "imgUrl", notes = "레시피 이미지url", example = "진미채.jpg")
    private String imgUrl;
    @ApiModelProperty(value = "title", notes = "레시피 타이틀", example = "진미채")
    private String title;
    @ApiModelProperty(value = "isAI", notes = "AI 레시피 여부", example = "true")
    private Boolean isAI;
    @ApiModelProperty(value = "likeCnt", notes = "좋아요 수", example = "10")
    private int likeCnt;
    @ApiModelProperty(value = "isLike", notes = "user의 좋아요 여부", example = "true")
    private Boolean isLike;
    @ApiModelProperty(value = "level", notes = "레시피 난이도", example = "1")
    private int level;

    public RecipesDto(Recipe recipe, HeaderDto headerDto) {
        this.recipeId = recipe.getRecipeId();
        this.imgUrl = recipe.getResultImage();
        this.title = recipe.getRecipeTitle();
        this.isAI = recipe.isAi();
        this.likeCnt = recipe.getRecipeLikeCnt();
        this.isLike = false;
        for (RecipeLike recipeLike : recipe.getRecipeLikeList()){
            if (recipeLike.getUser().getEmail().equals(headerDto.getUserName())){
                this.isLike = true;
                break;
            }
        }
        this.level = recipe.getDifficulty();
    }
}
