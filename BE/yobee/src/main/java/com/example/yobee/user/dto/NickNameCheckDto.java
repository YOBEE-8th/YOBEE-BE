package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NickNameCheckDto {
    @ApiModelProperty(name = "nickname", value = "닉네임", example = "요비마스터")
    private String nickname;
}
