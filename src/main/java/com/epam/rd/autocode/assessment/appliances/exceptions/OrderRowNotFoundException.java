package com.epam.rd.autocode.assessment.appliances.exceptions;

public class OrderRowNotFoundException extends RuntimeException {
    
    public OrderRowNotFoundException(String message) {
        super(message);
    }
    
    public OrderRowNotFoundException(Long id) {
        super("Order row not found with id: " + id);
    }
}