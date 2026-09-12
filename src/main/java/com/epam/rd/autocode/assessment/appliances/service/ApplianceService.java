package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ApplianceService {

    PageResponseDto<ApplianceResponseDto> findAll(Pageable pageable);

    ApplianceResponseDto findById(Long id);

    ApplianceResponseDto create(@Valid ApplianceRequestDto requestDto);

    ApplianceResponseDto update(Long id, @Valid ApplianceRequestDto requestDto);

    void delete(Long id);

    List<CategoryStatsDto> getCategoryStatistics();

    PageResponseDto<ApplianceResponseDto> findByManufacturerAndPriceRange(
            String manufacturerName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );
}