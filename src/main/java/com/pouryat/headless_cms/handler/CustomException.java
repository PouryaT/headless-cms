package com.pouryat.headless_cms.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {

    private int statusCode;

    public CustomException() {
    }

    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}