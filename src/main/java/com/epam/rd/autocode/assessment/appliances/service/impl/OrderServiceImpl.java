package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Provider;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public List<OrderResponseDto> findAllForUser(Authentication authentication) {
        return List.of();
    }

    @Override
    public OrderResponseDto findByIdForUser(Long id, Authentication authentication) {
        return null;
    }

    @Override
    public OrderResponseDto create(OrderRequestDto requestDto, Authentication authentication) {
        return null;
    }

    @Override
    public OrderResponseDto update(Long id, OrderRequestDto requestDto, Authentication authentication) {
        return null;
    }

    @Override
    public void delete(Long id, Authentication authentication) {

    }

    @Override
    public OrderResponseDto approve(Long id, Authentication authentication) {
        return null;
    }
}
