package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ApplianceRequestDto(
        @NotBlank(message = "{appliance.name.notBlank}")
        String name,

        @NotNull(message = "{appliance.category.notNull}")
        @Enumerated(EnumType.STRING)
        Category category,

        @NotBlank(message = "{appliance.model.notBlank}")
        String model,

        @NotNull(message = "{appliance.manufacturer.notNull}")
        ManufacturerEntity manufacturer,

        @NotNull(message = "{appliance.powerType.notNull}")
        PowerType powerType,

        String characteristic,

        String description,

        @Positive(message = "{appliance.power.positive}")
        Integer power,

        @NotNull(message = "{appliance.price.notNull}")
        @Positive(message = "{appliance.price.positive}")
        BigDecimal price
) {
}
