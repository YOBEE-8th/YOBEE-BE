package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateUserInfoDto {

    @ApiModelProperty(name = "nickname", value = "닉네임", example = "요비마스터")
    private String nickname;

    @ApiModelProperty(name = "profileImage", value = "프로필 이미지", example = "프로필 이미지 파일")
    private MultipartFile profileImage;

}
