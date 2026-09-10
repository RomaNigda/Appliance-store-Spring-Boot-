package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.AuthRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.AuthResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.RegisterRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.UserInfoDto;
import com.epam.rd.autocode.assessment.appliances.entity.*;
import com.epam.rd.autocode.assessment.appliances.exceptions.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.RoleRepository;
import com.epam.rd.autocode.assessment.appliances.security.jwt.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * Реєстрація нового клієнта
     */
    @Transactional
    public AuthResponseDto registerClient(RegisterRequestDto request) {
        log.debug("Registering new client: {}", request.email());

        // Перевірка, чи email вже використовується
        if (clientRepository.existsByEmail(request.email()) ||
                employeeRepository.existsByEmail(request.email())) {
            log.warn("Email already registered: {}", request.email());
            throw new DuplicateEmailException("Email already registered: " + request.email());
        }

        // Пошук існуючої ролі в БД
        RoleEntity clientRole = (RoleEntity) roleRepository.findByName(RoleName.ROLE_CLIENT)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + RoleName.ROLE_CLIENT));

        // Створення клієнта
        ClientEntity client = new ClientEntity();
        client.setName(request.name());
        client.setEmail(request.email());
        client.setPassword(passwordEncoder.encode(request.password()));
        client.setCard(request.card());

        // Присвоюємо кешовану сутність з БД (ВИДАЛЕНО client.addRole(RoleName.ROLE_CLIENT))
//        client.getRoles().add(clientRole);
        client.addRole(clientRole);

        ClientEntity saved = clientRepository.save(client);
        log.debug("Client registered successfully with id: {}. Client = {}", saved.getId(), saved.toString());

        // Генерація токенів
        String accessToken = jwtService.generateAccessToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);

        AuthResponseDto responseDto = new AuthResponseDto(
                accessToken,
                refreshToken,
                jwtService.extractExpiration(accessToken).getTime(),
                toUserInfoDto(saved)
        );

        log.debug("Response from service: {}", responseDto);

        return responseDto;
    }

    /**
     * Реєстрація нового працівника
     */
    @Transactional
    public AuthResponseDto registerEmployee(RegisterRequestDto request) {
        log.debug("Registering new employee: {}", request.email());

        // Перевірка, чи email вже використовується
        if (clientRepository.existsByEmail(request.email()) ||
                employeeRepository.existsByEmail(request.email())) {
            log.warn("Email already registered: {}", request.email());
            throw new DuplicateEmailException("Email already registered: " + request.email());
        }

        // Пошук існуючої ролі в БД
        RoleEntity employeeRole = (RoleEntity) roleRepository.findByName(RoleName.ROLE_EMPLOYEE)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + RoleName.ROLE_EMPLOYEE));

        // Створення працівника
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setPassword(passwordEncoder.encode(request.password()));
        employee.setDepartment(request.department());

        // Присвоюємо кешовану сутність з БД (ВИДАЛЕНО employee.addRole(RoleName.ROLE_EMPLOYEE))
        employee.addRole(employeeRole);
//        employee.getRoles().add(employeeRole);

        EmployeeEntity saved = employeeRepository.save(employee);
        log.debug("Employee registered successfully with id: {}", saved.getId());

        // Генерація токенів
        String accessToken = jwtService.generateAccessToken(saved);
        String refreshToken = jwtService.generateRefreshToken(saved);

        return new AuthResponseDto(
                accessToken,
                refreshToken,
                jwtService.extractExpiration(accessToken).getTime(),
                toUserInfoDto(saved)
        );
    }

    /**
     * Логін користувача
     */
    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthRequestDto request) {
        log.debug("Authenticating user: {}", request.email());

        try {
            // Аутентифікація
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            log.debug("Authentication successful for user: {}. User roles: {}", request.email(),  authentication.getAuthorities());

            // Отримуємо користувача
            UserEntity user = getUserByEmail(request.email());

            // Генеруємо токени
            String accessToken = jwtService.generateAccessToken((UserDetails) authentication.getPrincipal());
            String refreshToken = jwtService.generateRefreshToken((UserDetails) authentication.getPrincipal());

            log.debug("User authenticated successfully: {}", request.email());

            return new AuthResponseDto(
                    accessToken,
                    refreshToken,
                    jwtService.extractExpiration(accessToken).getTime(),
                    toUserInfoDto(user)
            );
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.email(), e);
            throw e;
        }
    }

    /**
     * Оновлення токена
     */
    public AuthResponseDto refresh(String refreshToken) {
        log.debug("Refreshing token");

        String email = jwtService.extractEmail(refreshToken);
        UserEntity user = getUserByEmail(email);

        UserDetails userDetails = user;
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return new AuthResponseDto(
                newAccessToken,
                newRefreshToken,
                jwtService.extractExpiration(newAccessToken).getTime(),
                toUserInfoDto(user)
        );
    }

    private UserEntity getUserByEmail(String email) {
        return clientRepository.findByEmail(email)
                .map(UserEntity.class::cast)
                .orElseGet(() -> employeeRepository.findByEmail(email)
                        .map(UserEntity.class::cast)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email))
                );
    }

    private UserInfoDto toUserInfoDto(UserEntity user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return new UserInfoDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles
        );
    }
}