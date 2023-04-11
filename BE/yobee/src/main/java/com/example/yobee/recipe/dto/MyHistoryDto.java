package com.example.yobee.recipe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class MyHistoryDto {
    @ApiModelProperty(value = "reviewId", notes = "리뷰 id", example = "1")
    private Long reviewId;
    @ApiModelProperty(value = "reviewImage", notes = "리뷰 이미지", example = "내가만든제육.jpg")
    private String reviewImage;
    @ApiModelProperty(value = "title", notes = "레시피 제목?", example = "김치찌개?")
    private String title;
    @ApiModelProperty(value = "isCompleted", notes = "완성여부", example = "true")
    private Boolean isCompleted;
    @ApiModelProperty(value = "createdAt", notes = "작성시간", example = "2023-03-14 00000")
    private Date createdAt;
}
