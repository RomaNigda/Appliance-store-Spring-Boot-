package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.TopClientDto;
import com.epam.rd.autocode.assessment.appliances.entity.*;
import com.epam.rd.autocode.assessment.appliances.exceptions.*;
import com.epam.rd.autocode.assessment.appliances.mapper.OrderMapper;
import com.epam.rd.autocode.assessment.appliances.repository.*;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import com.epam.rd.autocode.assessment.appliances.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplianceRepository applianceRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<OrderResponseDto> findAllForCurrentUser(Pageable pageable) {
        UserEntity currentUser = securityUtils.getCurrentUser();
        log.debug("Fetching orders for user: {}, page: {}, size: {}",
                currentUser.getEmail(), pageable.getPageNumber(), pageable.getPageSize());

        Page<OrderEntity> page;

        if (currentUser.isEmployee()) {
            log.debug("Employee fetching all orders");
            page = orderRepository.findAll(pageable);
        } else if (currentUser.isClient()) {
            log.debug("Client fetching their own orders");
            page = orderRepository.findByClientId(currentUser.getId(), pageable);
        } else {
            throw new AccessDeniedException("User has no valid role");
        }

        log.debug("Found {} orders on page {} of {}",
                page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());

        return PageResponseDto.from(page, OrderResponseDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto findByIdForCurrentUser(Long id) {
        log.debug("Fetching order by id: {}", id);

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        UserEntity currentUser = securityUtils.getCurrentUser();

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

        if (currentUser.isClient()) {
            if (!currentUser.getId().equals(requestDto.clientId())) {
                log.warn("CLIENT SECURITY VIOLATION: Client {} attempted to create order for client {}",
                        currentUser.getId(), requestDto.clientId());
                throw new AccessDeniedException(
                        "You cannot create an order for another client. Your ID: " + currentUser.getId()
                );
            }
            log.debug("Client ID verification passed");
        }

        ClientEntity client = clientRepository.findById(requestDto.clientId())
                .orElseThrow(() -> new ClientNotFoundException(requestDto.clientId()));

        EmployeeEntity employee = employeeRepository.findById(requestDto.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(requestDto.employeeId()));

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

        if (entity.isApproved()) {
            log.warn("Attempt to update approved order: {}", id);
            throw new IllegalStateException("Cannot update an approved order");
        }

        UserEntity currentUser = securityUtils.getCurrentUser();

        if (currentUser.isClient()) {
            if (!entity.getClient().getId().equals(currentUser.getId())) {
                log.warn("CLIENT SECURITY VIOLATION: Client {} attempted to update order {} belonging to client {}",
                        currentUser.getId(), id, entity.getClient().getId());
                throw new AccessDeniedException("You cannot update an order that does not belong to you");
            }

            if (!currentUser.getId().equals(requestDto.clientId())) {
                log.warn("CLIENT SECURITY VIOLATION: Client {} attempted to change order client to {}",
                        currentUser.getId(), requestDto.clientId());
                throw new AccessDeniedException("You cannot change the client of an order");
            }
        }

        ClientEntity client = clientRepository.findById(requestDto.clientId())
                .orElseThrow(() -> new ClientNotFoundException(requestDto.clientId()));

        EmployeeEntity employee = employeeRepository.findById(requestDto.employeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(requestDto.employeeId()));

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

        if (entity.isApproved()) {
            log.warn("Attempt to delete approved order: {}", id);
            throw new IllegalStateException("Cannot delete an approved order");
        }

        UserEntity currentUser = securityUtils.getCurrentUser();

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

        UserEntity currentUser = securityUtils.getCurrentUser();
        if (!currentUser.isEmployee()) {
            log.warn("User {} attempted to approve order without employee role", currentUser.getEmail());
            throw new AccessDeniedException("Only employees can approve orders");
        }

        OrderEntity entity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

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