package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResponseGetExperienceDto {

    @ApiModelProperty(name = "soupExp", value = "soup 경험치", example = "1")
    private int soupExp;

    @ApiModelProperty(name = "sideExp", value = "side 경험치", example = "1")
    private int sideExp;

    @ApiModelProperty(name = "grilledExp", value = "grilled 경험치", example = "1")
    private int grilledExp;

    @ApiModelProperty(name = "dessertExp", value = "dessert 경험치", example = "1")
    private int dessertExp;

    @ApiModelProperty(name = "noodleExp", value = "noodle 경험치", example = "1")
    private int noodleExp;

}
