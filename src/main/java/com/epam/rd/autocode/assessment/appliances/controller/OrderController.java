package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.TopClientDto;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import com.epam.rd.autocode.assessment.appliances.web.PageableFactory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<PageResponseDto<OrderResponseDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageableFactory.forOrder(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(orderService.findAllForCurrentUser(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<OrderResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findByIdForCurrentUser(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.create(requestDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long id,
                                                   @Valid @RequestBody OrderRequestDto requestDto) {
        return ResponseEntity.ok(orderService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<OrderResponseDto> approve(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.approve(id));
    }

    @GetMapping("/stats/top-clients")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<TopClientDto>> getTopClients(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(orderService.findTopClients(limit));
    }
}