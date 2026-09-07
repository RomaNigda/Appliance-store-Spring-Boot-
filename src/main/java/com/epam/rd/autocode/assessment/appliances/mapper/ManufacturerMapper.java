package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerMapper {

    /**
     * Конвертує ManufacturerRequestDto → ManufacturerEntity
     */
    public ManufacturerEntity toEntity(ManufacturerRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ManufacturerEntity entity = new ManufacturerEntity();
        entity.setName(dto.name());

        return entity;
    }

    /**
     * Конвертує ManufacturerEntity → ManufacturerResponseDto
     */
    public ManufacturerResponseDto toResponseDto(ManufacturerEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ManufacturerResponseDto(
                entity.getId(),
                entity.getName()
        );
    }

    /**
     * Оновлює існуючу Entity з даних DTO (для PUT/UPDATE)
     */
    public void updateEntity(ManufacturerRequestDto dto, ManufacturerEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.name());
    }
}