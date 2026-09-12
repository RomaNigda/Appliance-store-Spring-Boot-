package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ApplianceRepository extends JpaRepository<ApplianceEntity, Long> {

    Page<ApplianceEntity> findAll(Pageable pageable);

    @Query("""
            SELECT a FROM ApplianceEntity a
            JOIN FETCH a.manufacturer m
            WHERE m.name = :manufacturerName
              AND a.price BETWEEN :minPrice AND :maxPrice
            """)
    Page<ApplianceEntity> findByManufacturerAndPriceRange(
            @Param("manufacturerName") String manufacturerName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("""
            SELECT 
                a.category,
                COUNT(a),
                AVG(a.price),
                MIN(a.price),
                MAX(a.price)
            FROM ApplianceEntity a
            GROUP BY a.category
            ORDER BY a.category
            """)
    List<Object[]> getCategoryStatisticsRaw();

    boolean existsByNameAndModel(@NotBlank(message = "{appliance.name.notBlank}") String name, @NotBlank(message = "{appliance.model.notBlank}") String model);
}