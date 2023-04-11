package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VerifyEmailDto {

    @ApiModelProperty(name = "emailToken", value = "이메일 인증번호", example = "80150")
    private String emailToken;
}


