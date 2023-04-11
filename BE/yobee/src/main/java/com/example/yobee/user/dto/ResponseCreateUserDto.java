package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResponseCreateUserDto {

    @ApiModelProperty(name = "accessToken", value = "액세스 토큰", example = "aaaaaa")
    private String accessToken;
    @ApiModelProperty(name = "refreshToken", value = "리프레시 토큰", example = "aaaaaaa")
    private String refreshToken;

    @ApiModelProperty(name = "userId", value = "유저id", example = "1")
    private Long userId;
    @ApiModelProperty(name = "email", value = "이메일", example = "ccc@google.com")
    private String email;
    @ApiModelProperty(name = "password", value = "비밀번호", example = "kkk1234!")
    private String password;
    @ApiModelProperty(name = "type", value = "소셜타입", example = "0")
    private int type;
    @ApiModelProperty(name = "fcmToken", value = "fcm토큰", example = "1223334")
    private String fcmToken;

    @ApiModelProperty(name = "nickname", value = "닉네임", example = "요비마스터")
    private String nickname;

    @ApiModelProperty(name = "profileImage", value = "프로필 이미지", example = "프로필 이미지 url")
    private String profileImage;
    @ApiModelProperty(name = "level", value = "유저 레벨", example = "0")
    private int level;


}
