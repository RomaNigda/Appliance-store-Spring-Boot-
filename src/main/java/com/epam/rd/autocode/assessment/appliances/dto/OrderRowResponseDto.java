package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.entity.OrderRowEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderRowResponseDto(

        @NotNull(message = "{orderRow.id.notNull}")
        @Positive(message = "{orderRow.id.positive}")
        Long id,

        @NotNull(message = "{orderRow.applianceId.notNull}")
        @Positive(message = "{orderRow.applianceId.positive}")
        Long applianceId,

        @NotBlank(message = "{orderRow.applianceName.notBlank}")
        String applianceName,

        @NotNull(message = "{orderRow.number.notNull}")
        @Positive(message = "{orderRow.number.positive}")
        Long number,

        @NotNull(message = "{orderRow.amount.notNull}")
        @Positive(message = "{orderRow.amount.positive}")
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