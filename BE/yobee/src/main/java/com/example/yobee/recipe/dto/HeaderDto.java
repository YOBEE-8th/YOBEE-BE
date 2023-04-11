package com.example.yobee.recipe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class HeaderDto {
    @ApiModelProperty(value = "UserName", notes = "유저이메일", example = "ccc@gmail.com")
    private String UserName;

}
