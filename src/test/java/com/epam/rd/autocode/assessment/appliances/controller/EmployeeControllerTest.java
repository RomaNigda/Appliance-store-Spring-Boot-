package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.exceptions.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
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
@DisplayName("EmployeeController Tests")
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeRequestDto requestDto;
    private EmployeeResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new EmployeeRequestDto(
                "Jane Employee",
                "jane@employee.com",
                "password123",
                "IT Department"
        );

        responseDto = new EmployeeResponseDto(
                1L,
                "Jane Employee",
                "jane@employee.com",
                "encodedPassword",
                "IT Department"
        );
    }

    // ============ FIND ALL ============

    @Nested
    @DisplayName("GET /api/employees")
    class FindAllTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return page of employees")
        void shouldReturnPageOfEmployees() throws Exception {
            PageResponseDto<EmployeeResponseDto> pageResponse = new PageResponseDto<>(
                    List.of(responseDto), 0, 20, 1, 1, true, true
            );

            when(employeeService.findAll(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/employees")
                            .param("page", "0")
                            .param("size", "20")
                            .param("sortBy", "name")
                            .param("sortDirection", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].name").value("Jane Employee"))
                    .andExpect(jsonPath("$.content[0].email").value("jane@employee.com"))
                    .andExpect(jsonPath("$.content[0].department").value("IT Department"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(employeeService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return empty page when no employees")
        void shouldReturnEmptyPage() throws Exception {
            PageResponseDto<EmployeeResponseDto> emptyPage = new PageResponseDto<>(
                    List.of(), 0, 20, 0, 0, true, true
            );

            when(employeeService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/employees"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(employeeService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/employees"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(employeeService);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Should return 403 when user is CLIENT")
        void shouldReturn403WhenClient() throws Exception {
            mockMvc.perform(get("/api/employees"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(employeeService);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("GET /api/employees/{id}")
    class FindByIdTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return employee by id")
        void shouldReturnEmployeeById() throws Exception {
            when(employeeService.findById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/employees/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Jane Employee"))
                    .andExpect(jsonPath("$.email").value("jane@employee.com"))
                    .andExpect(jsonPath("$.department").value("IT Department"));

            verify(employeeService, times(1)).findById(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(employeeService.findById(anyLong()))
                    .thenThrow(new EmployeeNotFoundException(999L));

            mockMvc.perform(get("/api/employees/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(employeeService, times(1)).findById(999L);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("POST /api/employees")
    class CreateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should create employee and return 201")
        void shouldCreateEmployee() throws Exception {
            when(employeeService.create(any(EmployeeRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/employees")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Jane Employee"))
                    .andExpect(jsonPath("$.email").value("jane@employee.com"))
                    .andExpect(jsonPath("$.department").value("IT Department"));

            verify(employeeService, times(1)).create(any(EmployeeRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            EmployeeRequestDto invalidRequest = new EmployeeRequestDto(
                    "", "jane@employee.com", "password123", "IT Department"
            );

            mockMvc.perform(post("/api/employees")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(employeeService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            EmployeeRequestDto invalidRequest = new EmployeeRequestDto(
                    "Jane Employee", "invalid-email", "password123", "IT Department"
            );

            mockMvc.perform(post("/api/employees")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());

            verifyNoInteractions(employeeService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when password is too short")
        void shouldReturn400WhenPasswordShort() throws Exception {
            EmployeeRequestDto invalidRequest = new EmployeeRequestDto(
                    "Jane Employee", "jane@employee.com", "123", "IT Department"
            );

            mockMvc.perform(post("/api/employees")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());

            verifyNoInteractions(employeeService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when department is blank")
        void shouldReturn400WhenDepartmentBlank() throws Exception {
            EmployeeRequestDto invalidRequest = new EmployeeRequestDto(
                    "Jane Employee", "jane@employee.com", "password123", ""
            );

            mockMvc.perform(post("/api/employees")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.department").exists());

            verifyNoInteractions(employeeService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when duplicate email")
        void shouldReturn400WhenDuplicateEmail() throws Exception {
            when(employeeService.create(any(EmployeeRequestDto.class)))
                    .thenThrow(new IllegalArgumentException(
                            "Employee with email 'jane@employee.com' already exists"
                    ));

            mockMvc.perform(post("/api/employees")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.error").exists());

            verify(employeeService, times(1)).create(any(EmployeeRequestDto.class));
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("PUT /api/employees/{id}")
    class UpdateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should update employee")
        void shouldUpdateEmployee() throws Exception {
            when(employeeService.update(eq(1L), any(EmployeeRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/employees/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Jane Employee"))
                    .andExpect(jsonPath("$.department").value("IT Department"));

            verify(employeeService, times(1)).update(eq(1L), any(EmployeeRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when employee not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(employeeService.update(eq(999L), any(EmployeeRequestDto.class)))
                    .thenThrow(new EmployeeNotFoundException(999L));

            mockMvc.perform(put("/api/employees/{id}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());

            verify(employeeService, times(1)).update(eq(999L), any(EmployeeRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            EmployeeRequestDto invalidRequest = new EmployeeRequestDto(
                    "", "jane@employee.com", "password123", "IT Department"
            );

            mockMvc.perform(put("/api/employees/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(employeeService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when duplicate email")
        void shouldReturn400WhenDuplicateEmail() throws Exception {
            when(employeeService.update(eq(1L), any(EmployeeRequestDto.class)))
                    .thenThrow(new IllegalArgumentException(
                            "Employee with email 'existing@employee.com' already exists"
                    ));

            mockMvc.perform(put("/api/employees/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.error").exists());

            verify(employeeService, times(1)).update(eq(1L), any(EmployeeRequestDto.class));
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("DELETE /api/employees/{id}")
    class DeleteTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should delete employee and return 204")
        void shouldDeleteEmployee() throws Exception {
            doNothing().when(employeeService).delete(1L);

            mockMvc.perform(delete("/api/employees/{id}", 1L).with(csrf()))
                    .andExpect(status().isNoContent());

            verify(employeeService, times(1)).delete(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when employee not found")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new EmployeeNotFoundException(999L))
                    .when(employeeService).delete(999L);

            mockMvc.perform(delete("/api/employees/{id}", 999L).with(csrf()))
                    .andExpect(status().isNotFound());

            verify(employeeService, times(1)).delete(999L);
        }
    }
}