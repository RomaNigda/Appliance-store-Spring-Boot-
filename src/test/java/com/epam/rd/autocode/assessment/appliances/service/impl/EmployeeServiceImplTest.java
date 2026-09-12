package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImpl Tests")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeEntity employee;
    private EmployeeRequestDto requestDto;
    private EmployeeResponseDto responseDto;

    @BeforeEach
    void setUp() {
        employee = new EmployeeEntity();
        employee.setId(1L);
        employee.setName("Jane Employee");
        employee.setEmail("jane@employee.com");
        employee.setPassword("encodedPassword");
        employee.setDepartment("IT Department");

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
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Should return page of employees")
        void shouldReturnPageOfEmployees() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<EmployeeEntity> page = new PageImpl<>(List.of(employee), pageable, 1);

            when(employeeRepository.findAll(pageable)).thenReturn(page);
            when(employeeMapper.toResponseDto(employee)).thenReturn(responseDto);

            PageResponseDto<EmployeeResponseDto> result = employeeService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("Jane Employee");
            assertThat(result.content().get(0).department()).isEqualTo("IT Department");
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isTrue();

            verify(employeeRepository, times(1)).findAll(pageable);
            verify(employeeMapper, times(1)).toResponseDto(employee);
        }

        @Test
        @DisplayName("Should return empty page when no employees")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<EmployeeEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(employeeRepository.findAll(pageable)).thenReturn(emptyPage);

            PageResponseDto<EmployeeResponseDto> result = employeeService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
            assertThat(result.totalPages()).isEqualTo(0);

            verify(employeeRepository, times(1)).findAll(pageable);
            verifyNoInteractions(employeeMapper);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Should return employee by id")
        void shouldReturnEmployeeById() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeMapper.toResponseDto(employee)).thenReturn(responseDto);

            EmployeeResponseDto result = employeeService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Jane Employee");
            assertThat(result.email()).isEqualTo("jane@employee.com");
            assertThat(result.department()).isEqualTo("IT Department");

            verify(employeeRepository, times(1)).findById(1L);
            verify(employeeMapper, times(1)).toResponseDto(employee);
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.findById(999L))
                    .isInstanceOf(EmployeeNotFoundException.class)
                    .hasMessageContaining("999");

            verify(employeeRepository, times(1)).findById(999L);
            verifyNoInteractions(employeeMapper);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("Should create employee successfully")
        void shouldCreateEmployee() {
            when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
            when(employeeMapper.toEntity(requestDto)).thenReturn(employee);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(employeeRepository.save(employee)).thenReturn(employee);
            when(employeeMapper.toResponseDto(employee)).thenReturn(responseDto);

            EmployeeResponseDto result = employeeService.create(requestDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Jane Employee");
            assertThat(result.department()).isEqualTo("IT Department");

            verify(employeeRepository, times(1)).existsByEmail(anyString());
            verify(employeeMapper, times(1)).toEntity(requestDto);
            verify(passwordEncoder, times(1)).encode("password123");
            verify(employeeRepository, times(1)).save(employee);
            verify(employeeMapper, times(1)).toResponseDto(employee);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when duplicate email")
        void shouldThrowExceptionWhenDuplicate() {
            when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

            assertThatThrownBy(() -> employeeService.create(requestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists")
                    .hasMessageContaining("jane@employee.com");

            verify(employeeRepository, times(1)).existsByEmail(anyString());
            verify(employeeRepository, never()).save(any());
            verifyNoInteractions(employeeMapper);
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("Should update employee successfully with same password")
        void shouldUpdateEmployeeWithSamePassword() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(employeeRepository.save(employee)).thenReturn(employee);
            when(employeeMapper.toResponseDto(employee)).thenReturn(responseDto);

            EmployeeResponseDto result = employeeService.update(1L, requestDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);

            verify(employeeRepository, times(1)).findById(1L);
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
            verify(passwordEncoder, never()).encode(anyString());
            verify(employeeRepository, times(1)).save(employee);
        }

        @Test
        @DisplayName("Should update employee and encode new password when changed")
        void shouldUpdateEmployeeWithNewPassword() {
            EmployeeRequestDto updateRequest = new EmployeeRequestDto(
                    "Jane Employee",
                    "jane@employee.com",
                    "newPassword123",
                    "IT Department"
            );

            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(passwordEncoder.matches("newPassword123", "encodedPassword")).thenReturn(false);
            when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
            when(employeeRepository.save(employee)).thenReturn(employee);
            when(employeeMapper.toResponseDto(employee)).thenReturn(responseDto);

            EmployeeResponseDto result = employeeService.update(1L, updateRequest);

            assertThat(result).isNotNull();

            verify(passwordEncoder, times(1)).matches("newPassword123", "encodedPassword");
            verify(passwordEncoder, times(1)).encode("newPassword123");
            verify(employeeRepository, times(1)).save(employee);
        }

        @Test
        @DisplayName("Should update employee when email is the same")
        void shouldUpdateWhenEmailIsSame() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(employeeRepository.save(employee)).thenReturn(employee);
            when(employeeMapper.toResponseDto(employee)).thenReturn(responseDto);

            EmployeeResponseDto result = employeeService.update(1L, requestDto);

            assertThat(result).isNotNull();

            verify(employeeRepository, times(1)).findById(1L);
            // Email такий самий — не перевіряємо existsByEmail
            verify(employeeRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when new email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            EmployeeRequestDto updateRequest = new EmployeeRequestDto(
                    "Jane Employee",
                    "existing@employee.com",
                    "password123",
                    "IT Department"
            );

            when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
            when(employeeRepository.existsByEmail("existing@employee.com")).thenReturn(true);

            assertThatThrownBy(() -> employeeService.update(1L, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.update(999L, requestDto))
                    .isInstanceOf(EmployeeNotFoundException.class)
                    .hasMessageContaining("999");

            verify(employeeRepository, times(1)).findById(999L);
            verify(employeeRepository, never()).save(any());
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Should delete employee successfully")
        void shouldDeleteEmployee() {
            when(employeeRepository.existsById(1L)).thenReturn(true);
            doNothing().when(employeeRepository).deleteById(1L);

            employeeService.delete(1L);

            verify(employeeRepository, times(1)).existsById(1L);
            verify(employeeRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EmployeeNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(employeeRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> employeeService.delete(999L))
                    .isInstanceOf(EmployeeNotFoundException.class)
                    .hasMessageContaining("999");

            verify(employeeRepository, times(1)).existsById(999L);
            verify(employeeRepository, never()).deleteById(anyLong());
        }
    }
}