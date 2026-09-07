package com.epam.rd.autocode.assessment.appliances.exceptions;

public class ApplianceNotFoundException extends RuntimeException {

    public ApplianceNotFoundException(String message) {
        super(message);
    }

    public ApplianceNotFoundException(Long id) {
        super("Appliance not found with id: " + id);
    }
}