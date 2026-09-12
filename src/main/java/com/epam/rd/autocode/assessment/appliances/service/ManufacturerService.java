package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface ManufacturerService {

    PageResponseDto<ManufacturerResponseDto> findAll(Pageable pageable);

    ManufacturerResponseDto findById(Long id);

    ManufacturerResponseDto create(@Valid ManufacturerRequestDto requestDto);

    ManufacturerResponseDto update(Long id, @Valid ManufacturerRequestDto requestDto);

    void delete(Long id);

    ManufacturerEntity getManufacturerEntityById(Long id);
}