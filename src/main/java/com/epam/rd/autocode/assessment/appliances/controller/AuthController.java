package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.AuthRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.AuthResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.RegisterRequestDto;

import com.epam.rd.autocode.assessment.appliances.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/client")
    public ResponseEntity<AuthResponseDto> registerClient(@Valid @RequestBody RegisterRequestDto request) {
        log.debug("Registering new client: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerClient(request));
    }

    @PostMapping("/register/employee")
    public ResponseEntity<AuthResponseDto> registerEmployee(@Valid @RequestBody RegisterRequestDto request) {
        log.debug("Registering new employee: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerEmployee(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        log.debug("Login request for user: {}", request.email());
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody String refreshToken) {
        log.debug("Refresh token request");
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
}