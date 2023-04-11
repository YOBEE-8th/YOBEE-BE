package com.example.yobee.review.dto;

import com.example.yobee.recipe.domain.Recipe;
import com.example.yobee.review.domain.Review;
import com.example.yobee.user.domain.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewDto {
    @ApiModelProperty(value = "reviewId", notes = "리뷰 id", example = "5866")
    private Long reviewId;
    @ApiModelProperty(value = "user", notes = "리뷰 쓴 유저", example = "유저 객체")
    private User user;
    @ApiModelProperty(value = "recipe", notes = "레시피", example = "레시피객체")
    private Recipe recipe;
    @ApiModelProperty(value = "content", notes = "컨텐츠", example = "맛있어요")
    private String content;
    @ApiModelProperty(value = "refviewImage", notes = "리뷰 이미지", example = "제육.jpg")
    private String reviewImage;
    @ApiModelProperty(value = "reviewLikeCnt", notes = "리뷰좋아요 수", example = "7")
    private int reviewLikeCnt;
    @ApiModelProperty(value = "createdAt", notes = "리뷰 생성시간", example = "2023-03-15 000000")
    private Date createdAt;

    public Review toEntity() {
        Review build = Review.builder()
                .reviewId(reviewId)
                .content(content)
                .reviewImage(reviewImage)
                .reviewLikeCnt(0)
                .createdAt(new Date())
                .build();
        return build;
    }

}
