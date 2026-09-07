package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeResponseDto> findAll();

    EmployeeResponseDto findById(Long id);

    EmployeeResponseDto create(EmployeeRequestDto requestDto);

    EmployeeResponseDto update(Long id, EmployeeRequestDto requestDto);

    void delete(Long id);
}