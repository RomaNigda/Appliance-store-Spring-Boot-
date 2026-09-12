package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.ManufacturerService;
import com.epam.rd.autocode.assessment.appliances.service.impl.ManufacturerServiceImpl;
import com.epam.rd.autocode.assessment.appliances.web.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manufacturers")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor

@Validated
public class ManufacturerController {

    private final Logger log = LoggerFactory.getLogger(ManufacturerController.class);

    private final ManufacturerService manufacturerService;


    @GetMapping
    public ResponseEntity<PageResponseDto<ManufacturerResponseDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageableFactory.forManufacturer(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(manufacturerService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ManufacturerResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(manufacturerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ManufacturerResponseDto> create(@Valid @RequestBody ManufacturerRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(manufacturerService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ManufacturerResponseDto> update(@PathVariable Long id,
                                                          @Valid @RequestBody ManufacturerRequestDto requestDto) {
        return ResponseEntity.ok(manufacturerService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        manufacturerService.delete(id);
    }
}