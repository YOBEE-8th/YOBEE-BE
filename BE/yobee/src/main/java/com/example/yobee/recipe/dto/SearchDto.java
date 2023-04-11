package com.example.yobee.recipe.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SearchDto {
    @ApiModelProperty(value = "keyword", notes = "검색하려는 키워드", example = "제육")
    private String keyword;
    @ApiModelProperty(value = "sort", notes = "정렬기준 0좋아요1리뷰2난이도", example = "1")
    private int sort;
    @ApiModelProperty(value = "order", notes = "오름차순/내림차순", example = "true")
    private Boolean order;
    @ApiModelProperty(value = "isAI", notes = "true경우 ai 레시피만 반환", example = "true")
    private Boolean isAI;

}
