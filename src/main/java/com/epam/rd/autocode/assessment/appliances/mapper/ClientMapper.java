package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ClientEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientMapper {


    public ClientEntity toEntity(ClientRequestDto dto) {
        if (dto == null) {
            log.debug("ClientRequestDto is null, returning null entity");
            return null;
        }

        log.debug("Mapping ClientRequestDto to Entity: {}", dto);

        ClientEntity entity = new ClientEntity();
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setCard(dto.card());

        log.debug("Mapped Entity: {}", entity);
        return entity;
    }


    public ClientResponseDto toResponseDto(ClientEntity entity) {
        if (entity == null) {
            log.debug("ClientEntity is null, returning null response");
            return null;
        }

        log.debug("Mapping ClientEntity to Response DTO: {}", entity);

        return new ClientResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getCard()
        );
    }


    public void updateEntity(ClientRequestDto dto, ClientEntity entity) {
        if (dto == null || entity == null) {
            log.debug("Cannot update entity: dto or entity is null");
            return;
        }

        log.debug("Updating entity with DTO: {}", dto);

        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPassword(dto.password());
        entity.setCard(dto.card());

        log.debug("Updated entity: {}", entity);
    }
}