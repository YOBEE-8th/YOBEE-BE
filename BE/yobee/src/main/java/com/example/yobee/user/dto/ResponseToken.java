package com.example.yobee.user.dto;

import lombok.Data;

@Data
public class ResponseToken {
    String accessToken;
    String refreshToken;
}