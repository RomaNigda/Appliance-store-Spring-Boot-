package com.epam.rd.autocode.assessment.appliances.exceptions;

public class ManufacturerNotFoundException extends RuntimeException {

    public ManufacturerNotFoundException(String message) {
        super(message);
    }

    public ManufacturerNotFoundException(Long id) {
        super("Manufacturer not found with id: " + id);
    }
}