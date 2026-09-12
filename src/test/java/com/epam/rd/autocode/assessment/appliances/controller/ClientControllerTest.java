package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.exceptions.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ClientController Tests")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    private ClientRequestDto requestDto;
    private ClientResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new ClientRequestDto(
                "John Client",
                "john@client.com",
                "password123",
                "1234-5678-9012-3456"
        );

        responseDto = new ClientResponseDto(
                1L,
                "John Client",
                "john@client.com",
                "encodedPassword",
                "1234-5678-9012-3456"
        );
    }

    // ============ FIND ALL ============

    @Nested
    @DisplayName("GET /api/clients")
    class FindAllTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return page of clients")
        void shouldReturnPageOfClients() throws Exception {
            PageResponseDto<ClientResponseDto> pageResponse = new PageResponseDto<>(
                    List.of(responseDto), 0, 20, 1, 1, true, true
            );

            when(clientService.findAll(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/clients")
                            .param("page", "0")
                            .param("size", "20")
                            .param("sortBy", "name")
                            .param("sortDirection", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].name").value("John Client"))
                    .andExpect(jsonPath("$.content[0].email").value("john@client.com"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(clientService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return empty page when no clients")
        void shouldReturnEmptyPage() throws Exception {
            PageResponseDto<ClientResponseDto> emptyPage = new PageResponseDto<>(
                    List.of(), 0, 20, 0, 0, true, true
            );

            when(clientService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(clientService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(clientService);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Should return 403 when user is CLIENT")
        void shouldReturn403WhenClient() throws Exception {
            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(clientService);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("GET /api/clients/{id}")
    class FindByIdTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return client by id")
        void shouldReturnClientById() throws Exception {
            when(clientService.findById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/clients/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("John Client"))
                    .andExpect(jsonPath("$.email").value("john@client.com"))
                    .andExpect(jsonPath("$.card").value("1234-5678-9012-3456"));

            verify(clientService, times(1)).findById(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(clientService.findById(anyLong()))
                    .thenThrow(new ClientNotFoundException(999L));

            mockMvc.perform(get("/api/clients/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(clientService, times(1)).findById(999L);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("POST /api/clients")
    class CreateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should create client and return 201")
        void shouldCreateClient() throws Exception {
            when(clientService.create(any(ClientRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/clients")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("John Client"))
                    .andExpect(jsonPath("$.email").value("john@client.com"));

            verify(clientService, times(1)).create(any(ClientRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            ClientRequestDto invalidRequest = new ClientRequestDto(
                    "", "john@client.com", "password123", "1234-5678-9012-3456"
            );

            mockMvc.perform(post("/api/clients")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(clientService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            ClientRequestDto invalidRequest = new ClientRequestDto(
                    "John Client", "invalid-email", "password123", "1234-5678-9012-3456"
            );

            mockMvc.perform(post("/api/clients")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());

            verifyNoInteractions(clientService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when password is too short")
        void shouldReturn400WhenPasswordShort() throws Exception {
            ClientRequestDto invalidRequest = new ClientRequestDto(
                    "John Client", "john@client.com", "123", "1234-5678-9012-3456"
            );

            mockMvc.perform(post("/api/clients")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());

            verifyNoInteractions(clientService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when duplicate email")
        void shouldReturn400WhenDuplicateEmail() throws Exception {
            when(clientService.create(any(ClientRequestDto.class)))
                    .thenThrow(new IllegalArgumentException(
                            "Client with email 'john@client.com' already exists"
                    ));

            mockMvc.perform(post("/api/clients")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.error").exists());

            verify(clientService, times(1)).create(any(ClientRequestDto.class));
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("PUT /api/clients/{id}")
    class UpdateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should update client")
        void shouldUpdateClient() throws Exception {
            when(clientService.update(eq(1L), any(ClientRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/clients/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("John Client"));

            verify(clientService, times(1)).update(eq(1L), any(ClientRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when client not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(clientService.update(eq(999L), any(ClientRequestDto.class)))
                    .thenThrow(new ClientNotFoundException(999L));

            mockMvc.perform(put("/api/clients/{id}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());

            verify(clientService, times(1)).update(eq(999L), any(ClientRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            ClientRequestDto invalidRequest = new ClientRequestDto(
                    "", "john@client.com", "password123", "1234-5678-9012-3456"
            );

            mockMvc.perform(put("/api/clients/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(clientService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when duplicate email")
        void shouldReturn400WhenDuplicateEmail() throws Exception {
            when(clientService.update(eq(1L), any(ClientRequestDto.class)))
                    .thenThrow(new IllegalArgumentException(
                            "Client with email 'existing@client.com' already exists"
                    ));

            mockMvc.perform(put("/api/clients/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.error").exists());

            verify(clientService, times(1)).update(eq(1L), any(ClientRequestDto.class));
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("DELETE /api/clients/{id}")
    class DeleteTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should delete client and return 204")
        void shouldDeleteClient() throws Exception {
            doNothing().when(clientService).delete(1L);

            mockMvc.perform(delete("/api/clients/{id}", 1L).with(csrf()))
                    .andExpect(status().isNoContent());

            verify(clientService, times(1)).delete(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when client not found")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new ClientNotFoundException(999L))
                    .when(clientService).delete(999L);

            mockMvc.perform(delete("/api/clients/{id}", 999L).with(csrf()))
                    .andExpect(status().isNotFound());

            verify(clientService, times(1)).delete(999L);
        }
    }
}