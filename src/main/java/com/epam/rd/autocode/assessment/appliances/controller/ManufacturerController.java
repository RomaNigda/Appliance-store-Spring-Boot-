package com.epam.rd.autocode.assessment.appliances.controller;


import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import com.epam.rd.autocode.assessment.appliances.service.impl.ManufacturerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturers")
public class ManufacturerController {

    private final Logger log = LoggerFactory.getLogger(ManufacturerController.class);

    private final ManufacturerService manufacturerService;

    public ManufacturerController(ManufacturerServiceImpl manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<List<ManufacturerResponseDto>> findAll() {
        return ResponseEntity.ok(manufacturerService.findAll());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<ManufacturerResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturerService.findById(id));
    }

    @PostMapping
//    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ManufacturerResponseDto> create(@Valid @RequestBody ManufacturerRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(manufacturerService.create(requestDto));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ManufacturerResponseDto> update(@PathVariable Long id,
                                                           @Valid @RequestBody ManufacturerRequestDto requestDto) {
        return ResponseEntity.ok(manufacturerService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        manufacturerService.delete(id);
    }
}