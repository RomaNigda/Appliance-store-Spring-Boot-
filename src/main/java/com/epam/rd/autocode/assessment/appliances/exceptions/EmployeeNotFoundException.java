package com.epam.rd.autocode.assessment.appliances.exceptions;

public class EmployeeNotFoundException extends RuntimeException {
    
    public EmployeeNotFoundException(String message) {
        super(message);
    }
    
    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}