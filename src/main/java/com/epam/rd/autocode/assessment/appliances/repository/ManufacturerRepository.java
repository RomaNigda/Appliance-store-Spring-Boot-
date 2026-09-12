package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManufacturerRepository extends JpaRepository<ManufacturerEntity, Long> {

    Page<ManufacturerEntity> findAll(Pageable pageable);

    boolean existsByName(String name);

    Optional<ManufacturerEntity> findByName(String name);
}