package com.epam.rd.autocode.assessment.appliances.dto;

import java.util.Set;

public record UserInfoDto(
        Long id,
        String name,
        String email,
        Set<String> roles
) {
}