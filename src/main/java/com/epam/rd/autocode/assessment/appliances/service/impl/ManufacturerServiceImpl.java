package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ManufacturerNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ManufacturerMapper;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManufacturerServiceImpl implements ManufacturerService {

    private final ManufacturerRepository manufacturerRepository;
    private final ManufacturerMapper manufacturerMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ManufacturerResponseDto> findAll() {
        log.debug("Fetching all manufacturers");

        List<ManufacturerResponseDto> result = manufacturerRepository.findAll()
                .stream()
                .map(manufacturerMapper::toResponseDto)
                .toList();

        log.debug("Found {} manufacturers", result.size());
        return result;
    }

    @Override
    @Transactional
    public ManufacturerResponseDto create(ManufacturerRequestDto requestDto) {
        log.debug("Creating new manufacturer with request: {}", requestDto);
        log.debug("Manufacturer name: {}", requestDto.name());

        // DTO → Entity
        ManufacturerEntity entity = manufacturerMapper.toEntity(requestDto);
        log.debug("Mapped DTO to Entity: {}", entity);

        // Перевірка на дублікат (якщо потрібно)
        if (manufacturerRepository.existsByName(requestDto.name())) {
            log.warn("Attempt to create duplicate manufacturer with name: {}", requestDto.name());
            throw new IllegalArgumentException(
                    "Manufacturer with name '" + requestDto.name() + "' already exists"
            );
        }

        // Збереження
        log.debug("Saving manufacturer entity to database");
        ManufacturerEntity saved = manufacturerRepository.save(entity);
        log.debug("Manufacturer saved successfully with id: {}", saved.getId());

        // Entity → Response DTO
        ManufacturerResponseDto response = manufacturerMapper.toResponseDto(saved);
        log.debug("Mapped Entity to Response DTO: {}", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ManufacturerResponseDto findById(Long id) {
        log.debug("Fetching manufacturer by id: {}", id);

        ManufacturerEntity entity = manufacturerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Manufacturer not found with id: {}", id);
                    return new ManufacturerNotFoundException(id);
                });

        log.debug("Found manufacturer: {}", entity);

        ManufacturerResponseDto response = manufacturerMapper.toResponseDto(entity);
        log.debug("Mapped Entity to Response DTO: {}", response);

        return response;
    }

    @Override
    @Transactional
    public ManufacturerResponseDto update(Long id, ManufacturerRequestDto requestDto) {
        log.debug("Updating manufacturer with id: {}", id);
        log.debug("Update request: {}", requestDto);

        ManufacturerEntity entity = manufacturerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Manufacturer not found with id: {} for update", id);
                    return new ManufacturerNotFoundException(id);
                });

        log.debug("Current entity before update: {}", entity);
        log.debug("Current name: {}, New name: {}", entity.getName(), requestDto.name());

        // Перевірка на дублікат (якщо назва змінюється)
        if (!entity.getName().equals(requestDto.name()) &&
                manufacturerRepository.existsByName(requestDto.name())) {
            log.warn("Attempt to update manufacturer to duplicate name: {}", requestDto.name());
            throw new IllegalArgumentException(
                    "Manufacturer with name '" + requestDto.name() + "' already exists"
            );
        }

        // Оновлення Entity з DTO
        manufacturerMapper.updateEntity(requestDto, entity);
        log.debug("Entity after update: {}", entity);

        ManufacturerEntity updated = manufacturerRepository.save(entity);
        log.debug("Manufacturer updated successfully with id: {}", updated.getId());

        ManufacturerResponseDto response = manufacturerMapper.toResponseDto(updated);
        log.debug("Mapped updated Entity to Response DTO: {}", response);

        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting manufacturer with id: {}", id);

        if (!manufacturerRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent manufacturer with id: {}", id);
            throw new ManufacturerNotFoundException(id);
        }

        manufacturerRepository.deleteById(id);
        log.debug("Manufacturer with id: {} deleted successfully", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ManufacturerEntity getManufacturerEntityById(Long id) {
        log.debug("Fetching manufacturer entity by id: {}", id);

        ManufacturerEntity entity = manufacturerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Manufacturer entity not found with id: {}", id);
                    return new ManufacturerNotFoundException(id);
                });

        log.debug("Found manufacturer entity: {}", entity);
        return entity;
    }
}