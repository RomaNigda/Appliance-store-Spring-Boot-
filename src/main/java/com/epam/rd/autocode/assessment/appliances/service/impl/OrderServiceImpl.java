package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.TopClientDto;
import com.epam.rd.autocode.assessment.appliances.entity.ClientEntity;
import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import com.epam.rd.autocode.assessment.appliances.entity.OrderEntity;
import com.epam.rd.autocode.assessment.appliances.entity.UserEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.exceptions.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.exceptions.OrderNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrderMapper;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrderRepository;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import com.epam.rd.autocode.assessment.appliances.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> findAllForCurrentUser() {
        UserEntity currentUser = securityUtils.getCurrentUser();
        log.debug("Fetching all orders for user: {}", currentUser.getEmail());

        List<OrderEntity> orders;

        if (currentUser.isEmployee()) {
            // Employee бачить всі замовлення
            log.debug("Employee fetching all orders");
            orders = orderRepository.findAll();
        } else if (currentUser.isClient()) {
            // Client бачить тільки свої замовлення
            log.debug("Client fetching their own orders");
            orders = orderRepository.findByClientId(currentUser.getId());
        } else {
            throw new AccessDeniedException("User has no valid role");
        }

        log.debug("Found {} orders", orders.size());
        return orders.stream()
                .map(OrderResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto findByIdForCurrentUser(Long id) {
        log.debug("Fetching order by id: {}", id);

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        UserEntity currentUser = securityUtils.getCurrentUser();

        // Client може бачити тільки свої замовлення
        if (currentUser.isClient() && !entity.getClient().getId().equals(currentUser.getId())) {
            log.warn("Client {} attempted to access order {} belonging to client {}",
                    currentUser.getId(), id, entity.getClient().getId());
            throw new AccessDeniedException("You do not have permission to view this order");
        }

        return OrderResponseDto.fromEntity(entity);
    }

    @Override
    @Transactional
    public OrderResponseDto create(OrderRequestDto requestDto) {
        UserEntity currentUser = securityUtils.getCurrentUser();
        log.debug("Creating new order for user: {}", currentUser.getEmail());

        // 🔒 КЛЮЧОВА ПЕРЕВІРКА БЕЗПЕКИ
        if (currentUser.isClient()) {
            // Client може створювати замовлення ТІЛЬКИ зі своїм ID
            if (!currentUser.getId().equals(requestDto.clientId())) {
                log.warn("CLIENT SECURITY VIOLATION: Client {} attempted to create order for client {}",
                        currentUser.getId(), requestDto.clientId());
                throw new AccessDeniedException(
                        "You cannot create an order for another client. Your ID: " + currentUser.getId()
                );
            }
            log.debug("Client ID verification passed");
        }
        // Employee може створювати для будь-якого клієнта (перевірка не потрібна)

        // Перевіряємо існування клієнта
        ClientEntity client = clientRepository.findById(requestDto.clientId())
                .orElseThrow(() -> new ClientNotFoundException(requestDto.clientId()));

        // Перевіряємо існування працівника
        EmployeeEntity employee = employeeRepository.findById(requestDto.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(requestDto.employeeId()));

        // Створюємо замовлення через маппер
        OrderEntity entity = orderMapper.toEntity(requestDto);
        entity.setClient(client);
        entity.setEmployee(employee);
        entity.setApproved(false);

        log.debug("Saving order with {} rows", entity.getOrderRowSet().size());
        OrderEntity saved = orderRepository.save(entity);
        log.debug("Order saved with id: {}", saved.getId());

        return OrderResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public OrderResponseDto update(Long id, OrderRequestDto requestDto) {
        log.debug("Updating order with id: {}", id);

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // ❌ Заборонено оновлювати підтверджені замовлення
        if (entity.isApproved()) {
            log.warn("Attempt to update approved order: {}", id);
            throw new IllegalStateException("Cannot update an approved order");
        }

        UserEntity currentUser = securityUtils.getCurrentUser();

        // 🔒 КЛЮЧОВА ПЕРЕВІРКА БЕЗПЕКИ
        if (currentUser.isClient()) {
            // Client не може змінювати чужі замовлення
            if (!entity.getClient().getId().equals(currentUser.getId())) {
                log.warn("CLIENT SECURITY VIOLATION: Client {} attempted to update order {} belonging to client {}",
                        currentUser.getId(), id, entity.getClient().getId());
                throw new AccessDeniedException("You cannot update an order that does not belong to you");
            }

            // Client не може змінювати clientId в замовленні
            if (!currentUser.getId().equals(requestDto.clientId())) {
                log.warn("CLIENT SECURITY VIOLATION: Client {} attempted to change order client to {}",
                        currentUser.getId(), requestDto.clientId());
                throw new AccessDeniedException("You cannot change the client of an order");
            }
        }
        // Employee може оновлювати будь-яке замовлення (перевірка не потрібна)

        // Перевіряємо існування клієнта та працівника
        ClientEntity client = clientRepository.findById(requestDto.clientId())
                .orElseThrow(() -> new ClientNotFoundException(requestDto.clientId()));

        EmployeeEntity employee = employeeRepository.findById(requestDto.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(requestDto.employeeId()));

        // Оновлюємо замовлення через маппер
        orderMapper.updateEntity(requestDto, entity);
        entity.setClient(client);
        entity.setEmployee(employee);

        log.debug("Order updated with id: {}", id);
        OrderEntity updated = orderRepository.save(entity);

        return OrderResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting order with id: {}", id);

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // ❌ Заборонено видаляти підтверджені замовлення
        if (entity.isApproved()) {
            log.warn("Attempt to delete approved order: {}", id);
            throw new IllegalStateException("Cannot delete an approved order");
        }

        UserEntity currentUser = securityUtils.getCurrentUser();

        // 🔒 КЛЮЧОВА ПЕРЕВІРКА БЕЗПЕКИ
        if (currentUser.isClient()) {
            if (!entity.getClient().getId().equals(currentUser.getId())) {
                log.warn("CLIENT SECURITY VIOLATION: Client {} attempted to delete order {} belonging to client {}",
                        currentUser.getId(), id, entity.getClient().getId());
                throw new AccessDeniedException("You cannot delete an order that does not belong to you");
            }
        }

        orderRepository.deleteById(id);
        log.debug("Order with id: {} deleted successfully", id);
    }

    @Override
    @Transactional
    public OrderResponseDto approve(Long id) {
        log.debug("Approving order with id: {}", id);

        // ✅ @PreAuthorize("hasRole('EMPLOYEE')") в контролері вже перевірив роль
        // Додаткова перевірка на всяк випадок
        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!currentUser.isEmployee()) {
            log.warn("User {} attempted to approve order without employee role", currentUser.getEmail());
            throw new AccessDeniedException("Only employees can approve orders");
        }

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // ✅ Можна підтвердити тільки непідтверджене замовлення
        if (entity.isApproved()) {
            log.warn("Attempt to approve already approved order: {}", id);
            throw new IllegalStateException("Order is already approved");
        }

        entity.setApproved(true);
        OrderEntity saved = orderRepository.save(entity);
        log.debug("Order {} approved successfully", id);

        return OrderResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopClientDto> findTopClients(int limit) {
        log.debug("Finding top {} clients by total amount", limit);
        return orderRepository.findTopClientsByTotalAmount(PageRequest.of(0, limit));
    }
}