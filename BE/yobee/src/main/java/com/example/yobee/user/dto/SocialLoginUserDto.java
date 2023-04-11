package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SocialLoginUserDto {

    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "ccc@google.com")
    private String email;
    @ApiModelProperty(name = "type", value = "소셜타입", example = "0")
    private int type;

    @ApiModelProperty(name = "fcmToken", value = "fcm토큰값", example = "0")
    private String fcmToken;



}