package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record ManufacturerRequestDto(
        @NotBlank(message = "{manufacturer.name.notBlank}")
        String name
) {
}
