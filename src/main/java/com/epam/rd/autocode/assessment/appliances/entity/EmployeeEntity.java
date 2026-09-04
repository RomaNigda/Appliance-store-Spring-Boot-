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
@Table(name = "employees")
public class EmployeeEntity extends UserEntity {

    @NotBlank(message = "{employee.department.notBlank}")
    private String department;
}