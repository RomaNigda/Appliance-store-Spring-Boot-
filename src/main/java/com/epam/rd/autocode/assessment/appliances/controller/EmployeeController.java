package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.EmployeeRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.EmployeeResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.EmployeeService;
import com.epam.rd.autocode.assessment.appliances.web.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYEE')")
@Validated
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<PageResponseDto<EmployeeResponseDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageableFactory.forEmployee(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(employeeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> create(@Valid @RequestBody EmployeeRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(employeeService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> update(@PathVariable Long id,
                                                      @Valid @RequestBody EmployeeRequestDto requestDto) {
        return ResponseEntity.ok(employeeService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }
}