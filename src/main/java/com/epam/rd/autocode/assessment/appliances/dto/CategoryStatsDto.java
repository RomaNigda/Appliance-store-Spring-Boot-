package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.model.Category;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CategoryStatsDto(

        @NotNull(message = "{categoryStats.category.notNull}")
        Category category,

        @NotNull(message = "{categoryStats.count.notNull}")
        @PositiveOrZero(message = "{categoryStats.count.positiveOrZero}")
        Long count,

        @NotNull(message = "{categoryStats.avgPrice.notNull}")
        @PositiveOrZero(message = "{categoryStats.avgPrice.positiveOrZero}")
        BigDecimal avgPrice,

        @NotNull(message = "{categoryStats.minPrice.notNull}")
        @PositiveOrZero(message = "{categoryStats.minPrice.positiveOrZero}")
        BigDecimal minPrice,

        @NotNull(message = "{categoryStats.maxPrice.notNull}")
        @PositiveOrZero(message = "{categoryStats.maxPrice.positiveOrZero}")
        BigDecimal maxPrice
) {
}