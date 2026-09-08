package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRowRequestDto(
        @NotNull(message = "{orderRow.applianceId.notNull}")
        Long applianceId,

        @NotNull(message = "{orderRow.number.notNull}")
        @Positive(message = "{orderRow.number.positive}")
        Long number,

        @NotNull(message = "{orderRow.amount.notNull}")
        @Positive(message = "{orderRow.amount.positive}")
        BigDecimal amount
) {
}