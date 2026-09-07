package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplianceRepository extends JpaRepository<ApplianceEntity, Long> {
    boolean existsByNameAndModel(@NotBlank(message = "{appliance.name.notBlank}") String name,
                                 @NotBlank(message = "{appliance.model.notBlank}") String model);
}
