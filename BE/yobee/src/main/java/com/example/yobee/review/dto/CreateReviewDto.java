package com.example.yobee.review.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateReviewDto {
    @ApiModelProperty(value = "recipeId", notes = "리뷰 작성할 레시피 id", example = "2")
    private Long recipeId;
    @ApiModelProperty(value = "content", notes = "리뷰 내용", example = "맛있어요~")
    private String content;
    @ApiModelProperty(value = "email", notes = "유저 이메일", example = "yobee@gmail.com")
    private String email;
}
