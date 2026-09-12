package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    PageResponseDto<EmployeeResponseDto> findAll(Pageable pageable);

    EmployeeResponseDto findById(Long id);

    EmployeeResponseDto create(EmployeeRequestDto requestDto);

    EmployeeResponseDto update(Long id, EmployeeRequestDto requestDto);

    void delete(Long id);
}