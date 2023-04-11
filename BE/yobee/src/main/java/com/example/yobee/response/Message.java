package com.example.yobee.response;
import lombok.Data;

@Data
public class Message {

    private int status;
    private String message;
    private Object data;

    public Message() {
        this.status = 400;
        this.data = null;
        this.message = null;
    }


    public enum StatusEnum {

        OK(200, "OK"),
        BAD_REQUEST(400, "BAD_REQUEST"),
        NOT_FOUND(404, "NOT_FOUND"),
        INTERNAL_SERER_ERROR(500, "INTERNAL_SERVER_ERROR"),
        SOCIAL_ACCOUNT_NOT_EXIST(700,"SOCIAL_ACCOUNT_NOT_EXIST");

        int statusCode;
        String code;

        StatusEnum(int statusCode, String code) {
            this.statusCode = statusCode;
            this.code = code;
        }
    }
}