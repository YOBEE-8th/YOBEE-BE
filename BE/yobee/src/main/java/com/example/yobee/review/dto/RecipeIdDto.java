package com.example.yobee.review.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RecipeIdDto {
    @ApiModelProperty(value = "recipeId", notes = "레시피 id", example = "142")
    private Long recipeId;
}
