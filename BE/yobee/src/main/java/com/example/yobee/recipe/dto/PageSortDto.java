package com.example.yobee.recipe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PageSortDto {
    @ApiModelProperty(value = "category", notes = "카테고리", example = "soup")
    private String category;
    @ApiModelProperty(value = "sort", notes = "정렬방법", example = "0")
    private int sort;
    @ApiModelProperty(value = "order", notes = "true 오름차순", example = "true")
    private  Boolean order;
    @ApiModelProperty(value = "isAI", notes = "ai레시피 여부", example = "true")
    private  Boolean isAI;
    @ApiModelProperty(value = "page", notes = "page", example = "0")
    private  int page;

}