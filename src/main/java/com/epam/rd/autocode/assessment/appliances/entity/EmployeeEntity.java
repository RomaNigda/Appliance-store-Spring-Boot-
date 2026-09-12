package com.epam.rd.autocode.assessment.appliances.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "user_id")
public class EmployeeEntity extends UserEntity {

    @Column(name = "department")
    @NotBlank(message = "{employee.department.notBlank}")
    private String department;

}