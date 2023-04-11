package com.example.yobee.review.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateReviewDto {
    @ApiModelProperty(value = "reviewId", notes = "리뷰아이디", example = "6")
    private Long reviewId;
    @ApiModelProperty(value = "content", notes = "수정할 내용", example = "간단하고 쉬워요~")
    private String content;
}
