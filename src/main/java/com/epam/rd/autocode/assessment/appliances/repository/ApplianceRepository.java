package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto;
import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplianceRepository extends JpaRepository<ApplianceEntity, Long> {
    boolean existsByNameAndModel(@NotBlank(message = "{appliance.name.notBlank}") String name,
                                 @NotBlank(message = "{appliance.model.notBlank}") String model);



    @Query("""
        SELECT new com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto(
            a.category,
            COUNT(a),
            AVG(a.price),
            MIN(a.price),
            MAX(a.price)
        )
        FROM ApplianceEntity a
        GROUP BY a.category
        ORDER BY a.category
        """)
    List<CategoryStatsDto> getCategoryStatistics();

    @Query(value = """
        SELECT 
            a.category,
            COUNT(*) as count,
            AVG(a.price) as avg_price,
            MIN(a.price) as min_price,
            MAX(a.price) as max_price
        FROM appliances a
        GROUP BY a.category
        ORDER BY a.category
        """, nativeQuery = true)
    List<Object[]> getCategoryStatisticsRaw();
}
