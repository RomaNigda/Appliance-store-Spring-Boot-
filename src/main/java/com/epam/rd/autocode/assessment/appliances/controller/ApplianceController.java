package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appliances")
public class ApplianceController {

    private final ApplianceService applianceService;

    public ApplianceController(ApplianceService applianceService) {
        this.applianceService = applianceService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<List<ApplianceResponseDto>> findAll() {
        return ResponseEntity.ok(applianceService.findAll());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<ApplianceResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(applianceService.findById(id));
    }

    @PostMapping
//    @PreAuthorize("hasRole('EMPLOYEE')")

    public ResponseEntity<ApplianceResponseDto> create(@Valid @RequestBody ApplianceRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(applianceService.create(requestDto));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ApplianceResponseDto> update(@PathVariable Long id,
                                                        @Valid @RequestBody ApplianceRequestDto requestDto) {
        return ResponseEntity.ok(applianceService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('EMPLOYEE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        applianceService.delete(id);
    }
}