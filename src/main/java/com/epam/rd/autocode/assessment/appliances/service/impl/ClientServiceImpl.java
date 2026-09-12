package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ClientEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ClientMapper;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<ClientResponseDto> findAll(Pageable pageable) {
        log.debug("Fetching clients, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ClientEntity> page = clientRepository.findAll(pageable);

        log.debug("Found {} clients on page {} of {}",
                page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());

        return PageResponseDto.from(page, clientMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto findById(Long id) {
        log.debug("Fetching client by id: {}", id);

        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found with id: {}", id);
                    return new ClientNotFoundException(id);
                });

        return clientMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public ClientResponseDto create(ClientRequestDto requestDto) {
        log.debug("Creating new client with request: {}", requestDto);

        if (clientRepository.existsByEmail(requestDto.email())) {
            log.warn("Attempt to create duplicate client with email: {}", requestDto.email());
            throw new IllegalArgumentException(
                    "Client with email '" + requestDto.email() + "' already exists"
            );
        }

        ClientEntity entity = clientMapper.toEntity(requestDto);

        log.debug("Encoding password");
        entity.setPassword(passwordEncoder.encode(requestDto.password()));
        log.debug("Password encoded successfully");

        ClientEntity saved = clientRepository.save(entity);
        log.debug("Client saved successfully with id: {}", saved.getId());

        return clientMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ClientResponseDto update(Long id, ClientRequestDto requestDto) {
        log.debug("Updating client with id: {}", id);

        ClientEntity entity = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Client not found with id: {} for update", id);
                    return new ClientNotFoundException(id);
                });

        log.debug("Current client: {}", entity);

        if (!entity.getEmail().equals(requestDto.email()) &&
                clientRepository.existsByEmail(requestDto.email())) {
            log.warn("Attempt to update client to duplicate email: {}", requestDto.email());
            throw new IllegalArgumentException(
                    "Client with email '" + requestDto.email() + "' already exists"
            );
        }

        entity.setName(requestDto.name());
        entity.setEmail(requestDto.email());
        entity.setCard(requestDto.card());

        if (!passwordEncoder.matches(requestDto.password(), entity.getPassword())) {
            log.debug("Password changed, encoding new password");
            entity.setPassword(passwordEncoder.encode(requestDto.password()));
            log.debug("New password encoded successfully");
        } else {
            log.debug("Password unchanged, keeping existing hash");
        }

        ClientEntity updated = clientRepository.save(entity);
        log.debug("Client updated successfully with id: {}", updated.getId());

        return clientMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting client with id: {}", id);

        if (!clientRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent client with id: {}", id);
            throw new ClientNotFoundException(id);
        }

        clientRepository.deleteById(id);
        log.debug("Client with id: {} deleted successfully", id);
    }
}