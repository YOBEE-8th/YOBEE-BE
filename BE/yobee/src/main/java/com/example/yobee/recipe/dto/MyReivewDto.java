package com.example.yobee.recipe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MyReivewDto {
    @ApiModelProperty(value = "recipeId", notes = "레시피아이디", example = "1")
    private Long recipeId;
    @ApiModelProperty(value = "recipeImage", notes = "레시피 이미지", example = "제육.jpg")
    private String recipeImage;
    @ApiModelProperty(value = "title", notes = "타이틀", example = "대파제육볶음")
    private String title;
    @ApiModelProperty(value = "reviewCnt", notes = "레시피의 리뷰 수", example = "10")
    private int reviewCnt;

}
