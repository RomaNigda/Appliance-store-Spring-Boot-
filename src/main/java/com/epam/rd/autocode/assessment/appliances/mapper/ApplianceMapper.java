package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplianceMapper {

    private final ManufacturerService manufacturerService;
    private final ManufacturerMapper manufacturerMapper;


    public ApplianceEntity toEntity(ApplianceRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ApplianceEntity entity = new ApplianceEntity();
        entity.setName(dto.name());
        entity.setCategory(dto.category());
        entity.setModel(dto.model());

        ManufacturerEntity manufacturer = manufacturerService.getManufacturerEntityById(
                dto.manufacturer().getId()
        );
        entity.setManufacturer(manufacturer);

        entity.setPowerType(dto.powerType());
        entity.setCharacteristic(dto.characteristic());
        entity.setDescription(dto.description());
        entity.setPower(dto.power());
        entity.setPrice(dto.price());

        return entity;
    }


    public ApplianceResponseDto toResponseDto(ApplianceEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ApplianceResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getModel(),
                manufacturerMapper.toResponseDto(entity.getManufacturer()),
                entity.getPowerType(),
                entity.getCharacteristic(),
                entity.getDescription(),
                entity.getPower(),
                entity.getPrice()
        );
    }


    public void updateEntity(ApplianceRequestDto dto, ApplianceEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.name());
        entity.setCategory(dto.category());
        entity.setModel(dto.model());

        ManufacturerEntity manufacturer = manufacturerService.getManufacturerEntityById(
                dto.manufacturer().getId()
        );
        entity.setManufacturer(manufacturer);

        entity.setPowerType(dto.powerType());
        entity.setCharacteristic(dto.characteristic());
        entity.setDescription(dto.description());
        entity.setPower(dto.power());
        entity.setPrice(dto.price());
    }
}