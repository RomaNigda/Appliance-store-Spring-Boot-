package com.epam.rd.autocode.assessment.appliances.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Appliance {
    private Long id;
    private String name;
    private Category category;
    private String model;
    private Manufacturer manufacturer;
    private PowerType powerType;
    private String characteristic;
    private String description;
    private Integer power;
    private BigDecimal price;

    public Appliance(Long id, String name, Category category, String model,
                     Manufacturer manufacturer, PowerType powerType,
                     String characteristic, String description, Integer power) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.model = model;
        this.manufacturer = manufacturer;
        this.powerType = powerType;
        this.characteristic = characteristic;
        this.description = description;
        this.power = power;
    }
}