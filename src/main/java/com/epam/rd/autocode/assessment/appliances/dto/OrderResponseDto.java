package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.entity.OrderEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record OrderResponseDto(

        @NotNull(message = "{order.id.notNull}")
        @Positive(message = "{order.id.positive}")
        Long id,

        @NotNull(message = "{order.clientId.notNull}")
        @Positive(message = "{order.clientId.positive}")
        Long clientId,

        @NotBlank(message = "{order.clientName.notBlank}")
        String clientName,

        @NotNull(message = "{order.employeeId.notNull}")
        @Positive(message = "{order.employeeId.positive}")
        Long employeeId,

        @NotBlank(message = "{order.employeeName.notBlank}")
        String employeeName,

        @NotNull(message = "{order.orderRows.notNull}")
        @NotEmpty(message = "{order.orderRows.notEmpty}")
        @Valid
        List<OrderRowResponseDto> orderRows,

        boolean approved
) {
    public static OrderResponseDto fromEntity(OrderEntity entity) {
        return new OrderResponseDto(
                entity.getId(),
                entity.getClient().getId(),
                entity.getClient().getName(),
                entity.getEmployee().getId(),
                entity.getEmployee().getName(),
                entity.getOrderRowSet().stream()
                        .map(OrderRowResponseDto::fromEntity)
                        .toList(),
                entity.isApproved()
        );
    }
}