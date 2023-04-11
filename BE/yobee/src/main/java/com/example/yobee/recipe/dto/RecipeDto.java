package com.example.yobee.recipe.dto;

import com.example.yobee.recipe.domain.HashTag;
import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.recipe.domain.RecipeLike;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class RecipeDto {
    @ApiModelProperty(value = "title", notes = "레시피사이틀", example = "제육볶음")
    private String title;
    @ApiModelProperty(value = "image", notes = "레시피이미지", example = "제육.jpg")
    private  String image;
    @ApiModelProperty(value = "time", notes = "조리시간", example = "40")
    private String time;
    @ApiModelProperty(value = "level", notes = "조리난이도", example = "2")
    private int level;
    @ApiModelProperty(value = "servings", notes = "몇인분인지", example = "3")
    private String servings;
    @ApiModelProperty(value = "isAI", notes = "AI레시피 여부", example = "true")
    private Boolean isAI;
    @ApiModelProperty(value = "category", notes = "레시피 카테고리", example = "side")
    private String category;
    @ApiModelProperty(value = "likeCnt", notes = "좋아요 수", example = "147")
    private  int likeCnt;
    @ApiModelProperty(value = "reviewCnt", notes = "리뷰 수", example = "14")
    private  Long reviewCnt;
    @ApiModelProperty(value = "isLike", notes = "user의 좋아요 여부", example = "false")
    private  Boolean isLike;
    @ApiModelProperty(value = "hashTag", notes = "해쉬테그", example = "[백종원, 제육볶음]")
    private List<String> hashTag;



    public RecipeDto(Recipe recipe, Boolean isLike, Long reviewCnt) {
        this.title = recipe.getRecipeTitle();
        this.image = recipe.getResultImage();
        this.time = recipe.getTime();
        this.level = recipe.getDifficulty();
        this.servings = recipe.getServings();
        this.isAI = recipe.isAi();
        this.category = recipe.getCategory();
        this.likeCnt = recipe.getRecipeLikeCnt();
        this.isLike = isLike;
        this.reviewCnt = reviewCnt;
        List<String> temp = new ArrayList<>();
        for (HashTag hashTag : recipe.getHashTagList()){
            temp.add(hashTag.getTag());
        }
        this.hashTag = temp;
    }


}
