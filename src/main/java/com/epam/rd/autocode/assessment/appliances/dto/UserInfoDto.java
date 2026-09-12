package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record UserInfoDto(

        @NotNull(message = "{userInfo.id.notNull}")
        @Positive(message = "{userInfo.id.positive}")
        Long id,

        @NotBlank(message = "{userInfo.name.notBlank}")
        String name,

        @NotBlank(message = "{userInfo.email.notBlank}")
        @Email(message = "{userInfo.email.invalid}")
        String email,

        @NotNull(message = "{userInfo.roles.notNull}")
        @NotEmpty(message = "{userInfo.roles.notEmpty}")
        Set<String> roles
) {
}