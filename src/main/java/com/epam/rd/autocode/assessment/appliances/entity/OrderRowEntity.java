package com.epam.rd.autocode.assessment.appliances.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_rows")
public class OrderRowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @NotNull(message = "{orderRow.appliance.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appliance_id", nullable = false)
    private ApplianceEntity appliance;

    @NotNull(message = "{orderRow.number.notNull}")
    @Positive(message = "{orderRow.number.positive}")
    @Column(nullable = false)
    private Long number;

    @NotNull(message = "{orderRow.amount.notNull}")
    @Positive(message = "{orderRow.amount.positive}")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
}