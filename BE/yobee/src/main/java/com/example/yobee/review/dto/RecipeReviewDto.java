package com.example.yobee.review.dto;

import com.example.yobee.recipe.dto.HeaderDto;
import com.example.yobee.review.domain.Review;
import com.example.yobee.review.domain.ReviewLike;
import com.example.yobee.user.domain.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RecipeReviewDto {
    @ApiModelProperty(value = "reviewId", notes = "리뷰 아이디", example = "3")
    private Long reviewId;
    @ApiModelProperty(value = "profileImg", notes = "프로필이미지", example = "User.jpg")
    private String profileImg;
    @ApiModelProperty(value = "nickname", notes = "유저 닉네임", example = "bosung")
    private String nickname;
    @ApiModelProperty(value = "createdAt", notes = "리뷰 작성시간", example = "2023-03-09-12333")
    private Date createdAt;
    @ApiModelProperty(value = "reviewImg", notes = "리뷰 사진", example = "제육리뷰.jpg")
    private String reviewImg;
    @ApiModelProperty(value = "content", notes = "컨텐츠", example = "제육 최고")
    private String content;
    @ApiModelProperty(value = "likeCnt", notes = "좋아요 수", example = "5")
    private int likeCnt;
    @ApiModelProperty(value = "isLike", notes = "사용자의 좋아요 여부", example = "false")
    private Boolean isLike;
    @ApiModelProperty(value = "isMine", notes = "자신의 리뷰인지? ", example = "false")
    private  Boolean isMine;

    public RecipeReviewDto(Review review, Boolean isLike, HeaderDto headerDto){
        this.reviewId = review.getReviewId();
        this.profileImg = review.getUser().getProfileImage();
        this.nickname = review.getUser().getNickName();
        this.createdAt = review.getCreatedAt();
        this.reviewImg = review.getReviewImage();
        this.content = review.getContent();
        this.likeCnt = review.getReviewLikeCnt();
        this.isLike = isLike;
        this.isMine = review.getUser().getEmail().equals(headerDto.getUserName());
    }
}
