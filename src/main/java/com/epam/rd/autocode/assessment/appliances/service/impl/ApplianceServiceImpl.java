package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;
    private final ApplianceMapper applianceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ApplianceResponseDto> findAll() {
        log.debug("Finding all appliances");

        return applianceRepository.findAll().stream()
                .map(applianceMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApplianceResponseDto findById(Long id) {
        log.debug("Finding appliance by id: {}", id);

        ApplianceEntity entity = getApplianceEntityById(id);
        return applianceMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public ApplianceResponseDto create(ApplianceRequestDto requestDto) {
        log.debug("Creating appliance: {}", requestDto);

        // Перевірка на дублікат (якщо потрібно)
         if (applianceRepository.existsByNameAndModel(requestDto.name(), requestDto.model())) {
             throw new IllegalArgumentException("Appliance with this name and model already exists");
         }

        ApplianceEntity entity = applianceMapper.toEntity(requestDto);
        log.debug("Mapped entity: {}", entity);

        ApplianceEntity saved = applianceRepository.save(entity);
        log.debug("Saved appliance with id: {}", saved.getId());

        return applianceMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ApplianceResponseDto update(Long id, ApplianceRequestDto requestDto) {
        log.debug("Updating appliance with id: {}", id);

        ApplianceEntity entity = getApplianceEntityById(id);
        applianceMapper.updateEntity(requestDto, entity);

        ApplianceEntity updated = applianceRepository.save(entity);
        log.debug("Updated appliance with id: {}", updated.getId());

        return applianceMapper.toResponseDto(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting appliance with id: {}", id);

        if (!applianceRepository.existsById(id)) {
            throw new ApplianceNotFoundException(id);
        }
        applianceRepository.deleteById(id);
        log.debug("Deleted appliance with id: {}", id);
    }

    @Transactional(readOnly = true)
    public ApplianceEntity getApplianceEntityById(Long id) {
        return applianceRepository.findById(id)
                .orElseThrow(() -> new ApplianceNotFoundException(id));
    }
}