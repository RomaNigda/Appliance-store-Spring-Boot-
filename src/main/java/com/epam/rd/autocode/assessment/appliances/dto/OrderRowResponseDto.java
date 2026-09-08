package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.entity.OrderRowEntity;

import java.math.BigDecimal;

public record OrderRowResponseDto(
        Long id,
        Long applianceId,
        String applianceName,
        Long number,
        BigDecimal amount
) {
    public static OrderRowResponseDto fromEntity(OrderRowEntity entity) {
        return new OrderRowResponseDto(
                entity.getId(),
                entity.getAppliance().getId(),
                entity.getAppliance().getName(),
                entity.getNumber(),
                entity.getAmount()
        );
    }
}