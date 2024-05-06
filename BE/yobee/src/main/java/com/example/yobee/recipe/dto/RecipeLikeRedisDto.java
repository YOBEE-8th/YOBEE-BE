package com.example.yobee.recipe.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class RecipeLikeRedisDto {
    private boolean isLike;
    private int cnt;

    @Builder
    public RecipeLikeRedisDto(boolean isLike, int cnt){
        this.isLike = isLike;
        this.cnt = cnt;
    }
}
