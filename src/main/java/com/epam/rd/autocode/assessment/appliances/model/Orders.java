package com.epam.rd.autocode.assessment.appliances.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    private Long id;
    private Employee employee;
    private Client client;
    private Set<OrderRow> orderRowSet;
    private Boolean approved;
}