package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeeMapper {


    public EmployeeEntity toEntity(EmployeeRequestDto dto) {
        if (dto == null) {
            log.debug("EmployeeRequestDto is null, returning null entity");
            return null;
        }

        log.debug("Mapping EmployeeRequestDto to Entity: {}", dto);

        EmployeeEntity entity = new EmployeeEntity();
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setDepartment(dto.department());

        log.debug("Mapped Entity: {}", entity);
        return entity;
    }


    public EmployeeResponseDto toResponseDto(EmployeeEntity entity) {
        if (entity == null) {
            log.debug("EmployeeEntity is null, returning null response");
            return null;
        }

        log.debug("Mapping EmployeeEntity to Response DTO: {}", entity);

        return new EmployeeResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getDepartment()
        );
    }


    public void updateEntity(EmployeeRequestDto dto, EmployeeEntity entity) {
        if (dto == null || entity == null) {
            log.debug("Cannot update entity: dto or entity is null");
            return;
        }

        log.debug("Updating entity with DTO: {}", dto);

        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setDepartment(dto.department());

        log.debug("Updated entity: {}", entity);
    }
}