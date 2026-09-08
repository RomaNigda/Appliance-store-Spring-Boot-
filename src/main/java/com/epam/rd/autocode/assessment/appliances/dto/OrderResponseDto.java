package com.epam.rd.autocode.assessment.appliances.dto;

import com.epam.rd.autocode.assessment.appliances.entity.OrderEntity;

import java.util.List;

public record OrderResponseDto(
        Long id,
        Long clientId,
        String clientName,
        Long employeeId,
        String employeeName,
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