package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<List<OrderResponseDto>> findAll() {
        return ResponseEntity.ok(orderService.findAllForCurrentUser());
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
}