package com.pouryat.headless_cms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {

    private int statusCode;

    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}