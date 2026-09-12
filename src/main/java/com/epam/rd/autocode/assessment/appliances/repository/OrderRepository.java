package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.dto.TopClientDto;
import com.epam.rd.autocode.assessment.appliances.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findAll(Pageable pageable);

    Page<OrderEntity> findByClientId(Long clientId, Pageable pageable);

    Page<OrderEntity> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<OrderEntity> findByApproved(boolean approved, Pageable pageable);

    @Query("""
        SELECT new com.epam.rd.autocode.assessment.appliances.dto.TopClientDto(
            c.id,
            c.name,
            c.email,
            COUNT(DISTINCT o.id),
            COALESCE(SUM(r.amount), 0)
        )
        FROM OrderEntity o
        JOIN o.client c
        JOIN o.orderRowSet r
        GROUP BY c.id, c.name, c.email
        ORDER BY SUM(r.amount) DESC
        """)
    List<TopClientDto> findTopClientsByTotalAmount(Pageable pageable);
}