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
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "user_id")
public class EmployeeEntity extends UserEntity {

    @Column(name = "department")
    private String department;

    public EmployeeEntity(Long id, String name, String email, String password, String department) {
        super(id, name, email, password);
        this.department = department;
        this.addRole(RoleName.ROLE_EMPLOYEE);
    }
}