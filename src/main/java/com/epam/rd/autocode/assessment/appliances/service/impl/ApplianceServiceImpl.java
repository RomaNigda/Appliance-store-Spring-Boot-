package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplianceServiceImpl implements ApplianceService {

    private final ApplianceRepository applianceRepository;
    private final ApplianceMapper applianceMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<ApplianceResponseDto> findAll(Pageable pageable) {
        log.debug("Finding all appliances, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ApplianceEntity> page = applianceRepository.findAll(pageable);

        log.debug("Found {} appliances on page {} of {}",
                page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());

        return PageResponseDto.from(page, applianceMapper::toResponseDto);
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

    @Override
    @Transactional(readOnly = true)
    public List<CategoryStatsDto> getCategoryStatistics() {
        return applianceRepository.getCategoryStatisticsRaw().stream()
                .map(row -> new CategoryStatsDto(
                        Category.valueOf(row[0].toString()),
                        ((Number) row[1]).longValue(),
                        BigDecimal.valueOf(((Number) row[2]).doubleValue()),
                        (BigDecimal) row[3],
                        (BigDecimal) row[4]
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<ApplianceResponseDto> findByManufacturerAndPriceRange(
            String manufacturerName,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        log.debug("Finding appliances by manufacturer: {} and price range: {} - {}, page: {}",
                manufacturerName, minPrice, maxPrice, pageable.getPageNumber());

        Page<ApplianceEntity> page = applianceRepository
                .findByManufacturerAndPriceRange(manufacturerName, minPrice, maxPrice, pageable);

        log.debug("Found {} appliances on page {} of {}",
                page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());

        return PageResponseDto.from(page, applianceMapper::toResponseDto);
    }
}