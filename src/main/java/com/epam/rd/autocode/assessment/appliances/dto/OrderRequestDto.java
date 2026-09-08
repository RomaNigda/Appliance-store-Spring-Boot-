package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequestDto(
        @NotNull(message = "{order.clientId.notNull}")
        Long clientId,

        @NotNull(message = "{order.employeeId.notNull}")
        Long employeeId,

        @NotNull(message = "{order.orderRows.notNull}")
        List<OrderRowRequestDto> orderRows
) {
}