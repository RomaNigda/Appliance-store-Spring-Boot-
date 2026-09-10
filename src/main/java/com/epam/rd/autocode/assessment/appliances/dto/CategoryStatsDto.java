package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.model.Category;

import java.math.BigDecimal;

public record CategoryStatsDto(
        Category category,
        Long count,
        BigDecimal avgPrice,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}