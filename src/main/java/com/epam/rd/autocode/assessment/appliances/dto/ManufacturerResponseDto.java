package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ManufacturerResponseDto(
        @NotNull
        Long id,

        @NotBlank(message = "{manufacturer.name.notBlank}")
        String name
) {
}
