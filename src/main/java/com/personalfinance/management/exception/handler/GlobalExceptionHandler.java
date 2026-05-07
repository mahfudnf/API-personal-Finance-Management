package com.personalfinance.management.exception.handler;

import com.personalfinance.management.exception.custom.*;
import com.personalfinance.management.model.response.ErrorResponse;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtError(
            JwtException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                buildError(
                        HttpStatus.UNAUTHORIZED,
                        "Token tidak valid atau rusak",
                        request.getRequestURI()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return new ResponseEntity<>(
                buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                buildError(
                        HttpStatus.UNAUTHORIZED,
                        "email atau password salah",
                        request.getRequestURI()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .status(400)
                .errors("Invalid parameter")
                .message("Parameter tidak valid")
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request
    ){
        return new ResponseEntity<>(
                buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Terjadi kesalahan pada server",
                        request.getRequestURI()
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ErrorResponse buildError(HttpStatus status,String message,String path){
        return ErrorResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(status.value())
                .errors(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
