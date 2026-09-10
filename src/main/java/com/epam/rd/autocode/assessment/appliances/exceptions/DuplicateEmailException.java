package com.epam.rd.autocode.assessment.appliances.exceptions;

public class DuplicateEmailException extends RuntimeException {
    
    public DuplicateEmailException(String message) {
        super(message);
    }
}