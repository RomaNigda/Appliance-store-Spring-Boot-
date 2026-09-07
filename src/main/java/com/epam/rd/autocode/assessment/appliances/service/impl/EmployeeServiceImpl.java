package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Override
    public List<EmployeeResponseDto> findAll() {
        return List.of();
    }

    @Override
    public EmployeeResponseDto findById(Long id) {
        return null;
    }

    @Override
    public EmployeeResponseDto create(EmployeeRequestDto requestDto) {
        return null;
    }

    @Override
    public EmployeeResponseDto update(Long id, EmployeeRequestDto requestDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
