package com.personalfinance.management.controller.errorContoller;

import com.personalfinance.management.model.WebResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Object>> methodArgumentNotValidException(MethodArgumentNotValidException exception){

        HashMap<Object, Object> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.builder().errors(String.valueOf(errors)).build());
    }
    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> constraintViolationException(ConstraintViolationException exception){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(WebResponse.<String>builder().errors(exception.getMessage()).build());
    }

    @ExceptionHandler
    public ResponseEntity<WebResponse<String>> apiException(ResponseStatusException exception){
        return ResponseEntity.status(exception.getStatusCode()).
                body(WebResponse.<String>builder().errors(exception.getReason()).build());
    }
}
