package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateExperienceDto {

    @ApiModelProperty(name = "recipeId", value = "레시피 Id", example = "1")
    private Long recipeId;
}
