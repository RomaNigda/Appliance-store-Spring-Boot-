package com.epam.rd.autocode.assessment.appliances.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRow {
    private Long id;
    private Appliance appliance;
    private Long number;
    private BigDecimal amount;
}