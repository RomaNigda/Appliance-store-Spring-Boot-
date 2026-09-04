package com.epam.rd.autocode.assessment.appliances.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clients")
public class ClientEntity extends UserEntity {

    @NotBlank(message = "{client.card.notBlank}")
    private String card;
}