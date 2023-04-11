package com.example.yobee.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResponseUpdateExperienceDto {


    @ApiModelProperty(name = "soupExp", value = "국/찌개 경험치", example = "1")
    private int soupExp;

    @ApiModelProperty(name = "sideExp", value = "반찬 경험치", example = "1")
    private int sideExp;

    @ApiModelProperty(name = "grilledExp", value = "구이/볶음 경험치", example = "1")
    private int grilledExp;

    @ApiModelProperty(name = "dessertExp", value = "디저트 경험치", example = "1")
    private int dessertExp;

    @ApiModelProperty(name = "noodleExp", value = "면 경험치", example = "1")
    private int noodleExp;

    @ApiModelProperty(name = "upCategory", value = "올라간 경험치 카테고리", example = "soup")
    private String upCategory;

    @ApiModelProperty(name = "upExp", value = "올라간 경험치 포인트", example = "1")
    private int upExp;

}
