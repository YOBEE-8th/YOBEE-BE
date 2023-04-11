package com.example.yobee.recipe.dto;

import com.example.yobee.recipe.domain.RecipeStep;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StepDto {
    @ApiModelProperty(value = "fire", notes = "불세기", example = "1")
    private int fire;
    @ApiModelProperty(value = "timer", notes = "타이머 시간 ms 기준", example = "20000")
    private Long timer;
    @ApiModelProperty(value = "stepImg", notes = "단계별 이미지", example = "대파 다듬는 중.jpg")
    private String stepImg;
    @ApiModelProperty(value = "description", notes = "단계별 설명", example = "대파를 먹기좋은 사이즈로 잘라주세요")
    private String description;

    public StepDto(RecipeStep recipeStep) {
        this.fire = recipeStep.getFire();
        this.timer = recipeStep.getTimer();
        this.stepImg = recipeStep.getStepImage();
        this.description = recipeStep.getStepDescription();
    }
}
