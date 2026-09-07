package com.epam.rd.autocode.assessment.appliances.dto;

public record RegistrationRequestDto(
        String name,
        String email,
        String password,
        String card,  // для Client
        String department // для Employee
) {
}
