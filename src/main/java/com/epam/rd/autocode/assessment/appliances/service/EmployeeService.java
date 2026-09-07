package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponseDto> findAll();

    EmployeeResponseDto findById(Long id);

    EmployeeResponseDto create(@Valid EmployeeRequestDto requestDto);

    EmployeeResponseDto update(Long id, @Valid EmployeeRequestDto requestDto);

    void delete(Long id);
}
