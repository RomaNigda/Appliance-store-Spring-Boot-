package com.epam.rd.autocode.assessment.appliances.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{user.name.notBlank}")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "{user.email.notBlank}")
    @Email(message = "{user.email.invalid}")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "{user.password.notBlank}")
    @Size(min = 8, message = "{user.password.size}")
    @Column(nullable = false)
    private String password;
}