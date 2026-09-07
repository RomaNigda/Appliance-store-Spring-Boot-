package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {
    List<OrderResponseDto> findAllForUser(Authentication authentication);

    OrderResponseDto findByIdForUser(Long id, Authentication authentication);

    OrderResponseDto create(@Valid OrderRequestDto requestDto, Authentication authentication);

    OrderResponseDto update(Long id, @Valid OrderRequestDto requestDto, Authentication authentication);

    void delete(Long id, Authentication authentication);

    OrderResponseDto approve(Long id, Authentication authentication);
}
