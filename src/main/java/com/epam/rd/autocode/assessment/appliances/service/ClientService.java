package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;

import java.util.List;

public interface ClientService {

    List<ClientResponseDto> findAll();

    ClientResponseDto findById(Long id);

    ClientResponseDto create(ClientRequestDto requestDto);

    ClientResponseDto update(Long id, ClientRequestDto requestDto);

    void delete(Long id);
}