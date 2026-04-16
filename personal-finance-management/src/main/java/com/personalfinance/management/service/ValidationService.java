package com.personalfinance.management.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ValidationService {
    /*
    Class dan method ini bisa dugunakan apabila ingin menggunakan validasi manual,
    tidak memanfaatkan fitur @Validated dari springBoot
    */

//    @Autowired
//    private Validator validator;
//
//    public void validate(Object request){
//        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
//
//        if (!constraintViolations.isEmpty()){
//            throw new ConstraintViolationException(constraintViolations);
//        }
//    }
}
