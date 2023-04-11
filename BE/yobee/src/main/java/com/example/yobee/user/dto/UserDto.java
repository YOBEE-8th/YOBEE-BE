package com.example.yobee.user.dto;

import com.example.yobee.user.domain.User;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class UserDto {


    private Long userId;
    private String email;
    private String password;

    private int type;

    private String fcmToken;

    private String refreshToken;

    private String nickName;

    private String profileImage;

    private int level;

//    private String secret;

    public User toEntity() {
        User build = User.builder()
                .userId(userId)
                .email(email)
                .password(password)
                .type(type)
                .fcmToken(fcmToken)
                .refreshToken(refreshToken)
                .nickName(nickName)
                .profileImage(profileImage)
                .level(level)
                .build();
        return build;
    }
}
