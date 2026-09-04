package com.epam.rd.autocode.assessment.appliances.entity;


import com.epam.rd.autocode.assessment.appliances.model.Category;


import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "appliances")
public class ApplianceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{appliance.name.notBlank}")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "{appliance.category.notNull}")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @NotBlank(message = "{appliance.model.notBlank}")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "{appliance.manufacturer.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private ManufacturerEntity manufacturer;

    @NotNull(message = "{appliance.powerType.notNull}")
    @Enumerated(EnumType.STRING)
    @Column(name = "power_type", nullable = false)
    private PowerType powerType;

    private String characteristic;

    @Column(length = 1000)
    private String description;

    @Positive(message = "{appliance.power.positive}")
    private Integer power;

    @NotNull(message = "{appliance.price.notNull}")
    @Positive(message = "{appliance.price.positive}")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}