package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
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
@RequestMapping("/api/clients")
@PreAuthorize("hasRole('EMPLOYEE')")
@RequiredArgsConstructor
@Validated
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<PageResponseDto<ClientResponseDto>> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        Pageable pageable = PageableFactory.forClient(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(clientService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> create(@Valid @RequestBody ClientRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clientService.create(requestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> update(@PathVariable Long id,
                                                    @Valid @RequestBody ClientRequestDto requestDto) {
        return ResponseEntity.ok(clientService.update(id, requestDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }
}