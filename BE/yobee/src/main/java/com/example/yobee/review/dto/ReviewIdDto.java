package com.example.yobee.review.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReviewIdDto {
    @ApiModelProperty(value = "reviewId", notes = "리뷰 아이디", example = "3")
    private Long reviewId;
}
