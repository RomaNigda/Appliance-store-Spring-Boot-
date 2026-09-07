package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> findAll() {
        log.debug("Fetching all employees");

        List<EmployeeResponseDto> result = employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toResponseDto)
                .toList();

        log.debug("Found {} employees", result.size());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDto findById(Long id) {
        log.debug("Fetching employee by id: {}", id);

        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with id: {}", id);
                    return new EmployeeNotFoundException(id);
                });

        log.debug("Found employee: {}", entity);

        EmployeeResponseDto response = employeeMapper.toResponseDto(entity);
        log.debug("Mapped Entity to Response DTO: {}", response);

        return response;
    }

    @Override
    @Transactional
    public EmployeeResponseDto create(EmployeeRequestDto requestDto) {
        log.debug("Creating new employee with request: {}", requestDto);
        log.debug("Employee email: {}", requestDto.email());

        // Перевірка на дублікат email
        if (employeeRepository.existsByEmail(requestDto.email())) {
            log.warn("Attempt to create duplicate employee with email: {}", requestDto.email());
            throw new IllegalArgumentException(
                    "Employee with email '" + requestDto.email() + "' already exists"
            );
        }

        // DTO → Entity
        EmployeeEntity entity = employeeMapper.toEntity(requestDto);
        log.debug("Mapped DTO to Entity: {}", entity);

        // Хешування пароля перед збереженням
        log.debug("Encoding password for employee");
        entity.setPassword(passwordEncoder.encode(requestDto.password()));
        log.debug("Password encoded successfully");

        // Збереження
        log.debug("Saving employee entity to database");
        EmployeeEntity saved = employeeRepository.save(entity);
        log.debug("Employee saved successfully with id: {}", saved.getId());

        // Entity → Response DTO
        EmployeeResponseDto response = employeeMapper.toResponseDto(saved);
        log.debug("Mapped Entity to Response DTO: {}", response);

        return response;
    }

    @Override
    @Transactional
    public EmployeeResponseDto update(Long id, EmployeeRequestDto requestDto) {
        log.debug("Updating employee with id: {}", id);
        log.debug("Update request: {}", requestDto);

        EmployeeEntity entity = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with id: {} for update", id);
                    return new EmployeeNotFoundException(id);
                });

        log.debug("Current entity before update: {}", entity);
        log.debug("Current email: {}, New email: {}", entity.getEmail(), requestDto.email());

        // Перевірка на дублікат email (якщо email змінюється)
        if (!entity.getEmail().equals(requestDto.email()) &&
                employeeRepository.existsByEmail(requestDto.email())) {
            log.warn("Attempt to update employee to duplicate email: {}", requestDto.email());
            throw new IllegalArgumentException(
                    "Employee with email '" + requestDto.email() + "' already exists"
            );
        }

        // Оновлення полів (крім пароля)
        entity.setName(requestDto.name());
        entity.setEmail(requestDto.email());
        entity.setDepartment(requestDto.department());

        // Оновлюємо пароль тільки якщо він змінився
        if (!passwordEncoder.matches(requestDto.password(), entity.getPassword())) {
            log.debug("Password changed, encoding new password");
            entity.setPassword(passwordEncoder.encode(requestDto.password()));
            log.debug("New password encoded successfully");
        } else {
            log.debug("Password unchanged, keeping existing hash");
        }

        log.debug("Entity after update: {}", entity);

        EmployeeEntity updated = employeeRepository.save(entity);
        log.debug("Employee updated successfully with id: {}", updated.getId());

        EmployeeResponseDto response = employeeMapper.toResponseDto(updated);
        log.debug("Mapped updated Entity to Response DTO: {}", response);

        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Deleting employee with id: {}", id);

        if (!employeeRepository.existsById(id)) {
            log.warn("Attempt to delete non-existent employee with id: {}", id);
            throw new EmployeeNotFoundException(id);
        }

        employeeRepository.deleteById(id);
        log.debug("Employee with id: {} deleted successfully", id);
    }
}