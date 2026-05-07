package com.personalfinance.management.exception.custom;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }
}
