package com.example.yobee.recipe.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewDto {
    private LocalDateTime localDateTime;
    private long id;

    @Builder
    public ViewDto(LocalDateTime localDateTime, long id){
        this.localDateTime = localDateTime;
        this.id = id;
    }
}
