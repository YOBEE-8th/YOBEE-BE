package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckSendEmailDto {

    @ApiModelProperty(name = "email", value = "유저 계정 이메일", example = "ccc@google.com")
    private String email;
}
