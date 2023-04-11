package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ValidateTokenDto {

    @ApiModelProperty(name = "accessToken", value = "액세스 토큰", example = "aaaaaa")
    private String accessToken;
    @ApiModelProperty(name = "refreshToken", value = "리프레시 토큰", example = "aaaaaaa")
    private String refreshToken;
}
