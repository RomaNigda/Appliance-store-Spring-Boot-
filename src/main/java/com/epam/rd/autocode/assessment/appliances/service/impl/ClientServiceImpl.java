package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    @Override
    public List<ClientResponseDto> findAll() {
        return List.of();
    }

    @Override
    public ClientResponseDto findById(Long id) {
        return null;
    }

    @Override
    public ClientResponseDto create(ClientRequestDto requestDto) {
        return null;
    }

    @Override
    public ClientResponseDto update(Long id, ClientRequestDto requestDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
