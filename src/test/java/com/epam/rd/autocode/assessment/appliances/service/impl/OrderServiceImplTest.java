package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.TopClientDto;
import com.epam.rd.autocode.assessment.appliances.entity.*;
import com.epam.rd.autocode.assessment.appliances.exceptions.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.exceptions.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.exceptions.OrderNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.OrderMapper;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.OrderRepository;
import com.epam.rd.autocode.assessment.appliances.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Tests")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ApplianceRepository applianceRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private OrderServiceImpl orderService;

    private ClientEntity client;
    private EmployeeEntity employee;
    private OrderEntity order;
    private OrderRequestDto requestDto;

    private RoleEntity clientRole;
    private RoleEntity employeeRole;

    @BeforeEach
    void setUp() {
        // Створюємо RoleEntity для клієнта
        clientRole = new RoleEntity();
        clientRole.setId(1L);
        clientRole.setName(RoleName.ROLE_CLIENT);

        // Створюємо RoleEntity для працівника
        employeeRole = new RoleEntity();
        employeeRole.setId(2L);
        employeeRole.setName(RoleName.ROLE_EMPLOYEE);

        client = new ClientEntity();
        client.setId(1L);
        client.setName("John Client");
        client.setEmail("john@client.com");
        client.addRole(clientRole);  // ← RoleEntity

        employee = new EmployeeEntity();
        employee.setId(3L);
        employee.setName("Jane Employee");
        employee.setEmail("jane@employee.com");
        employee.addRole(employeeRole);  // ← RoleEntity

        order = new OrderEntity();
        order.setId(1L);
        order.setClient(client);
        order.setEmployee(employee);
        order.setApproved(false);

        requestDto = new OrderRequestDto(
                1L,
                3L,
                List.of(new OrderRowRequestDto(1L, 2L, new BigDecimal("3999.98")))
        );
    }

    // ============ FIND ALL FOR CURRENT USER ============

    @Nested
    @DisplayName("findAllForCurrentUser()")
    class FindAllForCurrentUserTests {

        @Test
        @DisplayName("Employee should see all orders")
        void employeeShouldSeeAllOrders() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<OrderEntity> page = new PageImpl<>(List.of(order), pageable, 1);

            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(orderRepository.findAll(pageable)).thenReturn(page);

            PageResponseDto<OrderResponseDto> result = orderService.findAllForCurrentUser(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1);

            verify(orderRepository, times(1)).findAll(pageable);
            verify(orderRepository, never()).findByClientId(anyLong(), any());
        }

        @Test
        @DisplayName("Client should see only their orders")
        void clientShouldSeeOnlyTheirOrders() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<OrderEntity> page = new PageImpl<>(List.of(order), pageable, 1);

            when(securityUtils.getCurrentUser()).thenReturn(client);
            when(orderRepository.findByClientId(1L, pageable)).thenReturn(page);

            PageResponseDto<OrderResponseDto> result = orderService.findAllForCurrentUser(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);

            verify(orderRepository, times(1)).findByClientId(1L, pageable);
            verify(orderRepository, never()).findAll(pageable);
        }

        @Test
        @DisplayName("Should return empty page when no orders")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<OrderEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(orderRepository.findAll(pageable)).thenReturn(emptyPage);

            PageResponseDto<OrderResponseDto> result = orderService.findAllForCurrentUser(pageable);

            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }
    }

    // ============ FIND BY ID FOR CURRENT USER ============

    @Nested
    @DisplayName("findByIdForCurrentUser()")
    class FindByIdForCurrentUserTests {

        @Test
        @DisplayName("Employee should see any order")
        void employeeShouldSeeAnyOrder() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(securityUtils.getCurrentUser()).thenReturn(employee);

            OrderResponseDto result = orderService.findByIdForCurrentUser(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Client should see their own order")
        void clientShouldSeeOwnOrder() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(securityUtils.getCurrentUser()).thenReturn(client);

            OrderResponseDto result = orderService.findByIdForCurrentUser(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Client should NOT see another client's order")
        void clientShouldNotSeeAnotherClientsOrder() {
            // 🔥 Створюємо іншого клієнта з RoleEntity
            RoleEntity anotherClientRole = new RoleEntity();
            anotherClientRole.setId(1L);
            anotherClientRole.setName(RoleName.ROLE_CLIENT);

            ClientEntity anotherClient = new ClientEntity();
            anotherClient.setId(2L);
            anotherClient.addRole(anotherClientRole);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(securityUtils.getCurrentUser()).thenReturn(anotherClient);

            assertThatThrownBy(() -> orderService.findByIdForCurrentUser(1L))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("permission");
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.findByIdForCurrentUser(999L))
                    .isInstanceOf(OrderNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("Client should create order with own ID")
        void clientShouldCreateOrderWithOwnId() {
            when(securityUtils.getCurrentUser()).thenReturn(client);
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
            when(orderMapper.toEntity(requestDto)).thenReturn(order);
            when(orderRepository.save(order)).thenReturn(order);

            OrderResponseDto result = orderService.create(requestDto);

            assertThat(result).isNotNull();
            verify(orderRepository, times(1)).save(order);
        }

        @Test
        @DisplayName("Client should NOT create order for another client")
        void clientShouldNotCreateOrderForAnotherClient() {
            OrderRequestDto invalidRequest = new OrderRequestDto(999L, 3L, List.of());

            when(securityUtils.getCurrentUser()).thenReturn(client);

            assertThatThrownBy(() -> orderService.create(invalidRequest))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("another client");

            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Employee should create order for any client")
        void employeeShouldCreateOrderForAnyClient() {
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
            when(orderMapper.toEntity(requestDto)).thenReturn(order);
            when(orderRepository.save(order)).thenReturn(order);

            OrderResponseDto result = orderService.create(requestDto);

            assertThat(result).isNotNull();
            verify(orderRepository, times(1)).save(order);
        }

        @Test
        @DisplayName("Should throw ClientNotFoundException when client not found")
        void shouldThrowExceptionWhenClientNotFound() {
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(clientRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.create(requestDto))
                    .isInstanceOf(ClientNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when employee not found")
        void shouldThrowExceptionWhenEmployeeNotFound() {
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(employeeRepository.findById(3L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.create(requestDto))
                    .isInstanceOf(EmployeeNotFoundException.class);
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("Employee should update any non-approved order")
        void employeeShouldUpdateAnyNonApprovedOrder() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(employeeRepository.findById(3L)).thenReturn(Optional.of(employee));
            when(orderRepository.save(order)).thenReturn(order);

            OrderResponseDto result = orderService.update(1L, requestDto);

            assertThat(result).isNotNull();
            verify(orderRepository, times(1)).save(order);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when order is approved")
        void shouldThrowExceptionWhenApproved() {
            order.setApproved(true);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.update(1L, requestDto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("approved");
        }

        @Test
        @DisplayName("Client should NOT update another client's order")
        void clientShouldNotUpdateAnotherClientsOrder() {
            // 🔥 Створюємо іншого клієнта з RoleEntity
            RoleEntity anotherClientRole = new RoleEntity();
            anotherClientRole.setId(1L);
            anotherClientRole.setName(RoleName.ROLE_CLIENT);

            ClientEntity anotherClient = new ClientEntity();
            anotherClient.setId(2L);
            anotherClient.addRole(anotherClientRole);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(securityUtils.getCurrentUser()).thenReturn(anotherClient);

            assertThatThrownBy(() -> orderService.update(1L, requestDto))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.update(999L, requestDto))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Employee should delete any non-approved order")
        void employeeShouldDeleteAnyNonApprovedOrder() {
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            doNothing().when(orderRepository).deleteById(1L);

            orderService.delete(1L);

            verify(orderRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when order is approved")
        void shouldThrowExceptionWhenApproved() {
            order.setApproved(true);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.delete(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("approved");

            verify(orderRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Client should NOT delete another client's order")
        void clientShouldNotDeleteAnotherClientsOrder() {
            // 🔥 Створюємо іншого клієнта з RoleEntity
            RoleEntity anotherClientRole = new RoleEntity();
            anotherClientRole.setId(1L);
            anotherClientRole.setName(RoleName.ROLE_CLIENT);

            ClientEntity anotherClient = new ClientEntity();
            anotherClient.setId(2L);
            anotherClient.addRole(anotherClientRole);

            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(securityUtils.getCurrentUser()).thenReturn(anotherClient);

            assertThatThrownBy(() -> orderService.delete(1L))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.delete(999L))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }

    // ============ APPROVE ============

    @Nested
    @DisplayName("approve()")
    class ApproveTests {

        @Test
        @DisplayName("Employee should approve order")
        void employeeShouldApproveOrder() {
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
            when(orderRepository.save(order)).thenReturn(order);

            OrderResponseDto result = orderService.approve(1L);

            assertThat(result).isNotNull();
            assertThat(order.isApproved()).isTrue();
            verify(orderRepository, times(1)).save(order);
        }

        @Test
        @DisplayName("Client should NOT approve order")
        void clientShouldNotApproveOrder() {
            when(securityUtils.getCurrentUser()).thenReturn(client);

            assertThatThrownBy(() -> orderService.approve(1L))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("employees");
        }

        @Test
        @DisplayName("Should throw IllegalStateException when already approved")
        void shouldThrowExceptionWhenAlreadyApproved() {
            order.setApproved(true);
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

            assertThatThrownBy(() -> orderService.approve(1L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already approved");
        }

        @Test
        @DisplayName("Should throw OrderNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(securityUtils.getCurrentUser()).thenReturn(employee);
            when(orderRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.approve(999L))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }

    // ============ TOP CLIENTS ============

    @Nested
    @DisplayName("findTopClients()")
    class FindTopClientsTests {

        @Test
        @DisplayName("Should return top clients")
        void shouldReturnTopClients() {
            List<TopClientDto> topClients = List.of(
                    new TopClientDto(1L, "John Client", "john@client.com", 2L, new BigDecimal("4799.97"))
            );

            when(orderRepository.findTopClientsByTotalAmount(PageRequest.of(0, 5)))
                    .thenReturn(topClients);

            List<TopClientDto> result = orderService.findTopClients(5);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).clientName()).isEqualTo("John Client");

            verify(orderRepository, times(1)).findTopClientsByTotalAmount(PageRequest.of(0, 5));
        }

        @Test
        @DisplayName("Should return empty list when no clients")
        void shouldReturnEmptyList() {
            when(orderRepository.findTopClientsByTotalAmount(PageRequest.of(0, 5)))
                    .thenReturn(Collections.emptyList());

            List<TopClientDto> result = orderService.findTopClients(5);

            assertThat(result).isEmpty();
        }
    }
}