package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface OrderService {

    List<OrderResponseDto> findAllForCurrentUser();

    OrderResponseDto findByIdForCurrentUser(Long id);

    OrderResponseDto create(@Valid OrderRequestDto requestDto);

    OrderResponseDto update(Long id, @Valid OrderRequestDto requestDto);

    void delete(Long id);

    OrderResponseDto approve(Long id);
}