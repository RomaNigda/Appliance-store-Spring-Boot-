package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.TopClientDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    PageResponseDto<OrderResponseDto> findAllForCurrentUser(Pageable pageable);

    OrderResponseDto findByIdForCurrentUser(Long id);

    OrderResponseDto create(@Valid OrderRequestDto requestDto);

    OrderResponseDto update(Long id, @Valid OrderRequestDto requestDto);

    void delete(Long id);

    OrderResponseDto approve(Long id);

    List<TopClientDto> findTopClients(int limit);
}