package com.pouryat.headless_cms.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionErrorResponse> handleResourceNotFoundException(CustomException ex) {
        ExceptionErrorResponse error = new ExceptionErrorResponse(ex.getMessage(), ex.getStatusCode(), new Date(), null);
        return new ResponseEntity<>(error, HttpStatusCode.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception) {
        return new ResponseEntity<>(new ExceptionErrorResponse(exception.getMessage() + " ,you cant access this endPoint ,RESTRICTED ENDPOINT!", 403, new Date(), null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionErrorResponse> handle(MethodArgumentNotValidException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        var errorResponse = ExceptionErrorResponse.builder()
                .errors(errors)
                .timestamp(new Date())
                .message("missing data requirements in request")
                .statusCode(status.value())
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }

}