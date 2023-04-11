package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChangePasswordDto {

    @ApiModelProperty(name = "password", value = "패스워드", example = "kkk1234!")
    private String password;
}
