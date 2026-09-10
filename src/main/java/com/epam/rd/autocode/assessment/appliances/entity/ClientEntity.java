package com.epam.rd.autocode.assessment.appliances.entity;

import jakarta.persistence.*;
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
    private String card;

}