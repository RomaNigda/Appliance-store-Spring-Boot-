package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ApplianceService {
    List<ApplianceResponseDto> findAll();

    ApplianceResponseDto findById(Long id);

    ApplianceResponseDto create(@Valid ApplianceRequestDto requestDto);

    ApplianceResponseDto update(Long id, @Valid ApplianceRequestDto requestDto);

    void delete(Long id);

    List<CategoryStatsDto> getCategoryStatistics();

}
