package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.exceptions.ManufacturerNotFoundException;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
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
@DisplayName("ManufacturerController Tests")
class ManufacturerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ManufacturerService manufacturerService;

    private ManufacturerRequestDto requestDto;
    private ManufacturerResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new ManufacturerRequestDto("Samsung Electronics");
        responseDto = new ManufacturerResponseDto(1L, "Samsung Electronics");
    }

    // ============ FIND ALL ============

    @Nested
    @DisplayName("GET /api/manufacturers")
    class FindAllTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return page of manufacturers")
        void shouldReturnPageOfManufacturers() throws Exception {
            PageResponseDto<ManufacturerResponseDto> pageResponse = new PageResponseDto<>(
                    List.of(responseDto), 0, 20, 1, 1, true, true
            );

            when(manufacturerService.findAll(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/manufacturers")
                            .param("page", "0")
                            .param("size", "20")
                            .param("sortBy", "name")
                            .param("sortDirection", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].name").value("Samsung Electronics"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(manufacturerService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return empty page when no manufacturers")
        void shouldReturnEmptyPage() throws Exception {
            PageResponseDto<ManufacturerResponseDto> emptyPage = new PageResponseDto<>(
                    List.of(), 0, 20, 0, 0, true, true
            );

            when(manufacturerService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/manufacturers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(manufacturerService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/manufacturers"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(manufacturerService);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Should return 403 when user is CLIENT")
        void shouldReturn403WhenClient() throws Exception {
            mockMvc.perform(get("/api/manufacturers"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(manufacturerService);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("GET /api/manufacturers/{id}")
    class FindByIdTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return manufacturer by id")
        void shouldReturnManufacturerById() throws Exception {
            when(manufacturerService.findById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/manufacturers/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Samsung Electronics"));

            verify(manufacturerService, times(1)).findById(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(manufacturerService.findById(anyLong()))
                    .thenThrow(new ManufacturerNotFoundException(999L));

            mockMvc.perform(get("/api/manufacturers/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(manufacturerService, times(1)).findById(999L);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("POST /api/manufacturers")
    class CreateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should create manufacturer and return 201")
        void shouldCreateManufacturer() throws Exception {
            when(manufacturerService.create(any(ManufacturerRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/manufacturers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Samsung Electronics"));

            verify(manufacturerService, times(1)).create(any(ManufacturerRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            ManufacturerRequestDto invalidRequest = new ManufacturerRequestDto("");

            mockMvc.perform(post("/api/manufacturers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(manufacturerService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is null")
        void shouldReturn400WhenNameNull() throws Exception {
            ManufacturerRequestDto invalidRequest = new ManufacturerRequestDto(null);

            mockMvc.perform(post("/api/manufacturers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(manufacturerService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when duplicate name")
        void shouldReturn400WhenDuplicate() throws Exception {
            when(manufacturerService.create(any(ManufacturerRequestDto.class)))
                    .thenThrow(new IllegalArgumentException("Manufacturer with name 'Samsung Electronics' already exists"));

            mockMvc.perform(post("/api/manufacturers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.error").exists());

            verify(manufacturerService, times(1)).create(any(ManufacturerRequestDto.class));
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("PUT /api/manufacturers/{id}")
    class UpdateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should update manufacturer")
        void shouldUpdateManufacturer() throws Exception {
            when(manufacturerService.update(eq(1L), any(ManufacturerRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/manufacturers/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Samsung Electronics"));

            verify(manufacturerService, times(1)).update(eq(1L), any(ManufacturerRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when manufacturer not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(manufacturerService.update(eq(999L), any(ManufacturerRequestDto.class)))
                    .thenThrow(new ManufacturerNotFoundException(999L));

            mockMvc.perform(put("/api/manufacturers/{id}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());

            verify(manufacturerService, times(1)).update(eq(999L), any(ManufacturerRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            ManufacturerRequestDto invalidRequest = new ManufacturerRequestDto("");

            mockMvc.perform(put("/api/manufacturers/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(manufacturerService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when new name already exists")
        void shouldReturn400WhenNameExists() throws Exception {
            when(manufacturerService.update(eq(1L), any(ManufacturerRequestDto.class)))
                    .thenThrow(new IllegalArgumentException("Manufacturer with name 'Existing' already exists"));

            mockMvc.perform(put("/api/manufacturers/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.error").exists());

            verify(manufacturerService, times(1)).update(eq(1L), any(ManufacturerRequestDto.class));
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("DELETE /api/manufacturers/{id}")
    class DeleteTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should delete manufacturer and return 204")
        void shouldDeleteManufacturer() throws Exception {
            doNothing().when(manufacturerService).delete(1L);

            mockMvc.perform(delete("/api/manufacturers/{id}", 1L).with(csrf()))
                    .andExpect(status().isNoContent());

            verify(manufacturerService, times(1)).delete(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when manufacturer not found")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new ManufacturerNotFoundException(999L))
                    .when(manufacturerService).delete(999L);

            mockMvc.perform(delete("/api/manufacturers/{id}", 999L).with(csrf()))
                    .andExpect(status().isNotFound());

            verify(manufacturerService, times(1)).delete(999L);
        }
    }
}