package com.example.yobee.review.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MyReviewDetailDto {
    @ApiModelProperty(value = "reviewImage", notes = "리뷰 이미지", example = "내가 만든 제육.jpg")
    private String reviewImage;
    @ApiModelProperty(value = "content", notes = "리뷰 내용", example = "쉬워요~~")
    private String content;
}
