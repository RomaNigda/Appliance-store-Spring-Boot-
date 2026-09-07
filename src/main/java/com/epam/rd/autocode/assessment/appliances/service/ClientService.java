package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import jakarta.validation.Valid;

import java.util.List;

public interface ClientService {
    List<ClientResponseDto> findAll();


    ClientResponseDto findById(Long id);

    ClientResponseDto create(@Valid ClientRequestDto requestDto);

    ClientResponseDto update(Long id, @Valid ClientRequestDto requestDto);

    void delete(Long id);
}
