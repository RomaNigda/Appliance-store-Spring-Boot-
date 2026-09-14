package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.epam.rd.autocode.assessment.appliances.web.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/appliances")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor

@Validated
public class ApplianceController {

    private final ApplianceService applianceService;

    @GetMapping
    public ResponseEntity<PageResponseDto<ApplianceResponseDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageableFactory.forAppliance(page, size, sortBy, sortDirection);        return ResponseEntity.ok(applianceService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplianceResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(applianceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ApplianceResponseDto> create(@Valid @RequestBody ApplianceRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(applianceService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplianceResponseDto> update(@PathVariable Long id,
                                                       @Valid @RequestBody ApplianceRequestDto requestDto) {
        return ResponseEntity.ok(applianceService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        applianceService.delete(id);
    }

    @GetMapping("/stats/categories")
    public ResponseEntity<List<CategoryStatsDto>> getCategoryStats() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(applianceService.getCategoryStatistics());
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDto<ApplianceResponseDto>> searchByManufacturerAndPrice(
            @RequestParam String manufacturer,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageableFactory.forAppliance(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(
                applianceService.findByManufacturerAndPriceRange(
                        manufacturer, minPrice, maxPrice, pageable
                )
        );
    }
}