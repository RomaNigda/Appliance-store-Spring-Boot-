package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequestDto(

        @NotBlank(message = "{registration.name.notBlank}")
        @Size(min = 2, max = 100, message = "{registration.name.size}")
        String name,

        @NotBlank(message = "{registration.email.notBlank}")
        @Email(message = "{registration.email.invalid}")
        @Size(max = 150, message = "{registration.email.size}")
        String email,

        @NotBlank(message = "{registration.password.notBlank}")
        @Size(min = 8, max = 100, message = "{registration.password.size}")
        String password,

        // Для Client (опціонально, бо для Employee не потрібно)
        String card,

        // Для Employee (опціонально, бо для Client не потрібно)
        String department
) {
}