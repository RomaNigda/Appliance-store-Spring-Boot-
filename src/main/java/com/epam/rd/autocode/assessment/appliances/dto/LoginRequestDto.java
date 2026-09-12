package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

        @NotBlank(message = "{login.email.notBlank}")
        @Email(message = "{login.email.invalid}")
        String email,

        @NotBlank(message = "{login.password.notBlank}")
        @Size(min = 8, max = 100, message = "{login.password.size}")
        String password
) {
}