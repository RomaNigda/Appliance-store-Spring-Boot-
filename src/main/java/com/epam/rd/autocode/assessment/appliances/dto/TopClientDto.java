package com.epam.rd.autocode.assessment.appliances.dto;

import java.math.BigDecimal;

public record TopClientDto(
        Long clientId,
        String clientName,
        String email,
        Long totalOrders,
        BigDecimal totalAmount
) {
}