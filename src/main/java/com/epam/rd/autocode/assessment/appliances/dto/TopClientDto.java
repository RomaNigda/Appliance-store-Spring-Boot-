package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record TopClientDto(

        @NotNull(message = "{topClient.clientId.notNull}")
        @Positive(message = "{topClient.clientId.positive}")
        Long clientId,

        @NotBlank(message = "{topClient.clientName.notBlank}")
        String clientName,

        @NotBlank(message = "{topClient.email.notBlank}")
        @Email(message = "{topClient.email.invalid}")
        String email,

        @NotNull(message = "{topClient.totalOrders.notNull}")
        @PositiveOrZero(message = "{topClient.totalOrders.positiveOrZero}")
        Long totalOrders,

        @NotNull(message = "{topClient.totalAmount.notNull}")
        @PositiveOrZero(message = "{topClient.totalAmount.positiveOrZero}")
        BigDecimal totalAmount
) {
}