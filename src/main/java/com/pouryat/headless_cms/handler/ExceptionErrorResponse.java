package com.pouryat.headless_cms.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ExceptionErrorResponse {
    private String message;
    private int statusCode;
    private Date timestamp;
    private List<String> errors;
}