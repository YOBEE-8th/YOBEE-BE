package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Data
public class CreateUserDto {
    @NotBlank
    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "ccc@google.com")
    private String email;

    @ApiModelProperty(name = "password", value = "패스워드", example = "kkk1234!")
    private String password;

    @ApiModelProperty(name = "type", value = "소셜타입", example = "0")
    private String type;

    @ApiModelProperty(name = "nickname", value = "닉네임", example = "요비마스터")
    private String nickname;

//    @ApiModelProperty(name = "profileImage", value = "프로필 이미지", example = "프로필 이미지 파일")
//    private MultipartFile profileImage;

    @ApiModelProperty(name = "profileImageUrl", value = "소셜 프로필 이미지 url", example = "이미지 url")
    private String profileImageUrl;
    @NotBlank
    @ApiModelProperty(name = "fcmToken", value = "fcm토큰값", example = "0")
    private String fcmToken;

//    @ApiModelProperty(name = "secret", value = "비밀키", example = "the world")
//    private String secret;


}