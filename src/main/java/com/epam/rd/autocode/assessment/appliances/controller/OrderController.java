package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.OrderRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.OrderResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<List<OrderResponseDto>> findAll(Authentication authentication) {
        // employee -> усі замовлення; client -> лише власні (логіка фільтрації - в сервісі)
        return ResponseEntity.ok(orderService.findAllForUser(authentication));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<OrderResponseDto> findById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.findByIdForUser(id, authentication));
    }

    @PostMapping
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<OrderResponseDto> create(@Valid @RequestBody OrderRequestDto requestDto,
                                    Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.create(requestDto, authentication));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    public ResponseEntity<OrderResponseDto> update(@PathVariable Long id,
                                                    @Valid @RequestBody OrderRequestDto requestDto,
                                                    Authentication authentication) {
        // сервіс сам перевіряє: не approved і належить цьому користувачу (якщо client)
        return ResponseEntity.ok(orderService.update(id, requestDto, authentication));
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'CLIENT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        orderService.delete(id, authentication);
    }

    @PostMapping("/{id}/approve")
//    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<OrderResponseDto> approve(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.approve(id, authentication));
    }
}