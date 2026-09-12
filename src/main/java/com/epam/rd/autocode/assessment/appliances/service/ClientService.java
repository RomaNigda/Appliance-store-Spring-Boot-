package com.epam.rd.autocode.assessment.appliances.service;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    PageResponseDto<ClientResponseDto> findAll(Pageable pageable);

    ClientResponseDto findById(Long id);

    ClientResponseDto create(ClientRequestDto requestDto);

    ClientResponseDto update(Long id, ClientRequestDto requestDto);

    void delete(Long id);
}