package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class LoginUserDto {

    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "ccc@google.com")
    private String email;
    @ApiModelProperty(name = "password", value = "패스워드", example = "kkk1234!")
    private String password;

    @ApiModelProperty(name = "fcmToken", value = "fcm토큰값", example = "0")
    private String fcmToken;

//    @ApiModelProperty(name = "secret", value = "비밀키", example = "the world")
//    private String secret;

}
