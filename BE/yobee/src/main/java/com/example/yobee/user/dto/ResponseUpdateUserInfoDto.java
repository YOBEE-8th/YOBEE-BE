package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ResponseUpdateUserInfoDto {

    @ApiModelProperty(name = "nickname", value = "닉네임", example = "요비마스터")
    private String nickname;

    @ApiModelProperty(name = "profileImage", value = "프로필 이미지 url", example = "https://yobee-bucket.s3.ap-northeast-2.amazonaws.com/sample.jpg")
    private String profileImage;

}
