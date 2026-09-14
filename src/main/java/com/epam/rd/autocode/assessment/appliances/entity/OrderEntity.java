package com.epam.rd.autocode.assessment.appliances.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "{order.employee.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    @NotNull(message = "{order.client.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderRowEntity> orderRowSet = new HashSet<>();

    @Column(nullable = false)
    private boolean approved = false;

    public void addOrderRow(OrderRowEntity orderRow) {
        orderRowSet.add(orderRow);
        orderRow.setOrder(this);
    }

    public void removeOrderRow(OrderRowEntity orderRow) {
        orderRowSet.remove(orderRow);
        orderRow.setOrder(null);
    }
}