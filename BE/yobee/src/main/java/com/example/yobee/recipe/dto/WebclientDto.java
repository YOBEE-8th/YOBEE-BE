package com.example.yobee.recipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebclientDto {
    @JsonProperty
    private CookRCP01Dto COOKRCP01;

}
