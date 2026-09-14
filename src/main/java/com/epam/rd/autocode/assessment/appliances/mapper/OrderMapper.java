package com.epam.rd.autocode.assessment.appliances.mapper;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowRequestDto;
import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import com.epam.rd.autocode.assessment.appliances.entity.OrderEntity;
import com.epam.rd.autocode.assessment.appliances.entity.OrderRowEntity;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ApplianceRepository applianceRepository;


    public OrderEntity toEntity(OrderRequestDto dto) {
        if (dto == null) {
            log.debug("OrderRequestDto is null, returning null entity");
            return null;
        }

        log.debug("Mapping OrderRequestDto to Entity: {}", dto);

        OrderEntity entity = new OrderEntity();
        entity.setApproved(false);

        for (OrderRowRequestDto rowDto : dto.orderRows()) {
            ApplianceEntity appliance = applianceRepository.findById(rowDto.applianceId())
                    .orElseThrow(() -> new RuntimeException("Appliance not found with id: " + rowDto.applianceId()));

            OrderRowEntity orderRow = new OrderRowEntity();
            orderRow.setAppliance(appliance);
            orderRow.setNumber(rowDto.number());
            orderRow.setAmount(rowDto.amount());
            entity.addOrderRow(orderRow);
        }

        log.debug("Mapped Entity with {} rows", entity.getOrderRowSet().size());
        return entity;
    }


    public void updateEntity(OrderRequestDto dto, OrderEntity entity) {
        if (dto == null || entity == null) {
            log.debug("Cannot update entity: dto or entity is null");
            return;
        }

        log.debug("Updating entity with DTO: {}", dto);

        entity.getOrderRowSet().clear();

        for (OrderRowRequestDto rowDto : dto.orderRows()) {
            ApplianceEntity appliance = applianceRepository.findById(rowDto.applianceId())
                    .orElseThrow(() -> new RuntimeException("Appliance not found with id: " + rowDto.applianceId()));

            OrderRowEntity orderRow = new OrderRowEntity();
            orderRow.setAppliance(appliance);
            orderRow.setNumber(rowDto.number());
            orderRow.setAmount(rowDto.amount());
            entity.addOrderRow(orderRow);
        }

        log.debug("Updated entity with {} rows", entity.getOrderRowSet().size());
    }
}