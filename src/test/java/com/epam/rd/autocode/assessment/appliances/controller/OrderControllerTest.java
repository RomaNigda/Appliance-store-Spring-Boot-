package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderRowResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.TopClientDto;
import com.epam.rd.autocode.assessment.appliances.exceptions.OrderNotFoundException;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("OrderController Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderRequestDto requestDto;
    private OrderResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new OrderRequestDto(
                1L,
                3L,
                List.of(new OrderRowRequestDto(1L, 2L, new BigDecimal("3999.98")))
        );

        responseDto = new OrderResponseDto(
                1L,
                1L,
                "John Client",
                3L,
                "Jane Employee",
                List.of(new OrderRowResponseDto(1L, 1L, "Samsung Fridge", 2L, new BigDecimal("3999.98"))),
                false
        );
    }

    // ============ FIND ALL ============

    @Nested
    @DisplayName("GET /api/orders")
    class FindAllTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Employee should get all orders")
        void employeeShouldGetAllOrders() throws Exception {
            PageResponseDto<OrderResponseDto> pageResponse = new PageResponseDto<>(
                    List.of(responseDto), 0, 20, 1, 1, true, true
            );

            when(orderService.findAllForCurrentUser(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].clientName").value("John Client"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(orderService, times(1)).findAllForCurrentUser(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Client should get their orders")
        void clientShouldGetTheirOrders() throws Exception {
            PageResponseDto<OrderResponseDto> pageResponse = new PageResponseDto<>(
                    List.of(responseDto), 0, 20, 1, 1, true, true
            );

            when(orderService.findAllForCurrentUser(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());

            verify(orderService, times(1)).findAllForCurrentUser(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/orders"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(orderService);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("GET /api/orders/{id}")
    class FindByIdTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return order by id")
        void shouldReturnOrderById() throws Exception {
            when(orderService.findByIdForCurrentUser(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/orders/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.clientName").value("John Client"))
                    .andExpect(jsonPath("$.employeeName").value("Jane Employee"));

            verify(orderService, times(1)).findByIdForCurrentUser(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(orderService.findByIdForCurrentUser(anyLong()))
                    .thenThrow(new OrderNotFoundException(999L));

            mockMvc.perform(get("/api/orders/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(orderService, times(1)).findByIdForCurrentUser(999L);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Client should get 403 when accessing another client's order")
        void clientShouldGet403WhenAccessingAnotherClientsOrder() throws Exception {
            when(orderService.findByIdForCurrentUser(1L))
                    .thenThrow(new AccessDeniedException("You do not have permission to view this order"));

            mockMvc.perform(get("/api/orders/{id}", 1L))
                    .andExpect(status().isForbidden());
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("POST /api/orders")
    class CreateTests {

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Client should create order and return 201")
        void clientShouldCreateOrder() throws Exception {
            when(orderService.create(any(OrderRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.clientName").value("John Client"));

            verify(orderService, times(1)).create(any(OrderRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Employee should create order and return 201")
        void employeeShouldCreateOrder() throws Exception {
            when(orderService.create(any(OrderRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated());

            verify(orderService, times(1)).create(any(OrderRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Client should get 403 when creating order for another client")
        void clientShouldGet403WhenCreatingForAnotherClient() throws Exception {
            OrderRequestDto invalidRequest = new OrderRequestDto(999L, 3L, List.of());

            when(orderService.create(any(OrderRequestDto.class)))
                    .thenThrow(new AccessDeniedException("You cannot create an order for another client"));

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Should return 400 when clientId is null")
        void shouldReturn400WhenClientIdNull() throws Exception {
            OrderRequestDto invalidRequest = new OrderRequestDto(null, 3L, List.of());

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(orderService);
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("PUT /api/orders/{id}")
    class UpdateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Employee should update order")
        void employeeShouldUpdateOrder() throws Exception {
            when(orderService.update(eq(1L), any(OrderRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/orders/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(orderService, times(1)).update(eq(1L), any(OrderRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(orderService.update(eq(999L), any(OrderRequestDto.class)))
                    .thenThrow(new OrderNotFoundException(999L));

            mockMvc.perform(put("/api/orders/{id}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when order is approved")
        void shouldReturn400WhenApproved() throws Exception {
            when(orderService.update(eq(1L), any(OrderRequestDto.class)))
                    .thenThrow(new IllegalStateException("Cannot update an approved order"));

            mockMvc.perform(put("/api/orders/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("DELETE /api/orders/{id}")
    class DeleteTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Employee should delete order and return 204")
        void employeeShouldDeleteOrder() throws Exception {
            doNothing().when(orderService).delete(1L);

            mockMvc.perform(delete("/api/orders/{id}", 1L).with(csrf()))
                    .andExpect(status().isNoContent());

            verify(orderService, times(1)).delete(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new OrderNotFoundException(999L)).when(orderService).delete(999L);

            mockMvc.perform(delete("/api/orders/{id}", 999L).with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when order is approved")
        void shouldReturn400WhenApproved() throws Exception {
            doThrow(new IllegalStateException("Cannot delete an approved order"))
                    .when(orderService).delete(1L);

            mockMvc.perform(delete("/api/orders/{id}", 1L).with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }

    // ============ APPROVE ============

    @Nested
    @DisplayName("POST /api/orders/{id}/approve")
    class ApproveTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Employee should approve order")
        void employeeShouldApproveOrder() throws Exception {
            OrderResponseDto approvedResponse = new OrderResponseDto(
                    1L, 1L, "John Client", 3L, "Jane Employee",
                    List.of(new OrderRowResponseDto(1L, 1L, "Samsung Fridge", 2L, new BigDecimal("3999.98"))),
                    true
            );

            when(orderService.approve(1L)).thenReturn(approvedResponse);

            mockMvc.perform(post("/api/orders/{id}/approve", 1L).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.approved").value(true));

            verify(orderService, times(1)).approve(1L);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Client should get 403 when approving")
        void clientShouldGet403WhenApproving() throws Exception {
            mockMvc.perform(post("/api/orders/{id}/approve", 1L).with(csrf()))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when already approved")
        void shouldReturn400WhenAlreadyApproved() throws Exception {
            when(orderService.approve(1L))
                    .thenThrow(new IllegalStateException("Order is already approved"));

            mockMvc.perform(post("/api/orders/{id}/approve", 1L).with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }

    // ============ TOP CLIENTS ============

    @Nested
    @DisplayName("GET /api/orders/stats/top-clients")
    class TopClientsTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Employee should get top clients")
        void employeeShouldGetTopClients() throws Exception {
            List<TopClientDto> topClients = List.of(
                    new TopClientDto(1L, "John Client", "john@client.com", 2L, new BigDecimal("4799.97"))
            );

            when(orderService.findTopClients(anyInt())).thenReturn(topClients);

            mockMvc.perform(get("/api/orders/stats/top-clients")
                            .param("limit", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].clientName").value("John Client"))
                    .andExpect(jsonPath("$[0].totalAmount").value(4799.97));

            verify(orderService, times(1)).findTopClients(5);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Client should get 403 for top clients")
        void clientShouldGet403ForTopClients() throws Exception {
            mockMvc.perform(get("/api/orders/stats/top-clients"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(orderService);
        }
    }
}