package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.EmployeeEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.EmployeeNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.EmployeeMapper;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<EmployeeResponseDto> findAll(Pageable pageable) {
        log.debug("Fetching employees, page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<EmployeeEntity> page = employeeRepository.findAll(pageable);

        log.debug("Found {} employees on page {} of {}",
                page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());

        return PageResponseDto.from(page, employeeMapper::toResponseDto);
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

        return employeeMapper.toResponseDto(entity);
    }

    @Override
    @Transactional
    public EmployeeResponseDto create(EmployeeRequestDto requestDto) {
        log.debug("Creating new employee with request: {}", requestDto);
        log.debug("Employee email: {}", requestDto.email());

        if (employeeRepository.existsByEmail(requestDto.email())) {
            log.warn("Attempt to create duplicate employee with email: {}", requestDto.email());
            throw new IllegalArgumentException(
                    "Employee with email '" + requestDto.email() + "' already exists"
            );
        }

        EmployeeEntity entity = employeeMapper.toEntity(requestDto);
        log.debug("Mapped DTO to Entity: {}", entity);

        log.debug("Encoding password for employee");
        entity.setPassword(passwordEncoder.encode(requestDto.password()));
        log.debug("Password encoded successfully");

        EmployeeEntity saved = employeeRepository.save(entity);
        log.debug("Employee saved successfully with id: {}", saved.getId());

        return employeeMapper.toResponseDto(saved);
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

        if (!entity.getEmail().equals(requestDto.email()) &&
                employeeRepository.existsByEmail(requestDto.email())) {
            log.warn("Attempt to update employee to duplicate email: {}", requestDto.email());
            throw new IllegalArgumentException(
                    "Employee with email '" + requestDto.email() + "' already exists"
            );
        }

        entity.setName(requestDto.name());
        entity.setEmail(requestDto.email());
        entity.setDepartment(requestDto.department());

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

        return employeeMapper.toResponseDto(updated);
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