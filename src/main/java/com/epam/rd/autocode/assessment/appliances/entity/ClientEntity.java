package com.epam.rd.autocode.assessment.appliances.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "clients")
@PrimaryKeyJoinColumn(name = "user_id")
public class ClientEntity extends UserEntity {

    @Column(name = "card")
    @NotBlank(message = "{client.card.notBlank}")
    private String card;

}