package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "{user.name.notBlank}")
        String name,

        @NotBlank(message = "{user.email.notBlank}")
        @Email(message = "{user.email.invalid}")
        String email,

        @NotBlank(message = "{user.password.notBlank}")
        @Size(min = 8, message = "{user.password.size}")
        String password,

        String department,  // Тільки для Employee
        String card         // Тільки для Client
) {
}