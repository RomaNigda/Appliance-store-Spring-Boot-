package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.AuthRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.AuthResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.RegisterRequestDto;
import com.epam.rd.autocode.assessment.appliances.entity.*;
import com.epam.rd.autocode.assessment.appliances.exceptions.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import com.epam.rd.autocode.assessment.appliances.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthService authService;

    private ClientEntity client;
    private EmployeeEntity employee;
    private RoleEntity clientRole;
    private RoleEntity employeeRole;

    @BeforeEach
    void setUp() {
        clientRole = new RoleEntity();
        clientRole.setId(1L);
        clientRole.setName(RoleName.ROLE_CLIENT);

        employeeRole = new RoleEntity();
        employeeRole.setId(2L);
        employeeRole.setName(RoleName.ROLE_EMPLOYEE);

        client = new ClientEntity();
        client.setId(1L);
        client.setName("John Client");
        client.setEmail("john@client.com");
        client.setPassword("encodedPassword");
        client.setCard("1234-5678-9012-3456");
        client.addRole(clientRole);

        employee = new EmployeeEntity();
        employee.setId(3L);
        employee.setName("Jane Employee");
        employee.setEmail("jane@employee.com");
        employee.setPassword("encodedPassword");
        employee.setDepartment("IT");
        employee.addRole(employeeRole);
    }

    // ============ REGISTER CLIENT ============

    @Nested
    @DisplayName("registerClient()")
    class RegisterClientTests {

        @Test
        @DisplayName("Should register client successfully")
        void shouldRegisterClient() {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "john@client.com", "password123",
                    null, "1234-5678-9012-3456"
            );

            when(clientRepository.existsByEmail(anyString())).thenReturn(false);
            when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByName(RoleName.ROLE_CLIENT)).thenReturn(Optional.of(clientRole));
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(clientRepository.save(any(ClientEntity.class))).thenReturn(client);
            when(jwtService.generateAccessToken(any())).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any())).thenReturn("refreshToken");
            when(jwtService.extractExpiration("accessToken")).thenReturn(new Date());

            AuthResponseDto result = authService.registerClient(request);

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo("accessToken");
            assertThat(result.refreshToken()).isEqualTo("refreshToken");
            assertThat(result.user().name()).isEqualTo("John Client");
            assertThat(result.user().roles()).contains("ROLE_CLIENT");

            verify(clientRepository, times(1)).existsByEmail("john@client.com");
            verify(employeeRepository, times(1)).existsByEmail("john@client.com");
            verify(roleRepository, times(1)).findByName(RoleName.ROLE_CLIENT);
            verify(passwordEncoder, times(1)).encode("password123");
            verify(clientRepository, times(1)).save(any(ClientEntity.class));
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email already exists in clients")
        void shouldThrowExceptionWhenEmailExistsInClients() {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "john@client.com", "password123",
                    null, "1234-5678-9012-3456"
            );

            when(clientRepository.existsByEmail("john@client.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerClient(request))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessageContaining("already registered");

            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email already exists in employees")
        void shouldThrowExceptionWhenEmailExistsInEmployees() {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "jane@employee.com", "password123",
                    null, "1234-5678-9012-3456"
            );

            when(clientRepository.existsByEmail("jane@employee.com")).thenReturn(false);
            when(employeeRepository.existsByEmail("jane@employee.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerClient(request))
                    .isInstanceOf(DuplicateEmailException.class);

            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when role not found")
        void shouldThrowExceptionWhenRoleNotFound() {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "john@client.com", "password123",
                    null, "1234-5678-9012-3456"
            );

            when(clientRepository.existsByEmail(anyString())).thenReturn(false);
            when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByName(RoleName.ROLE_CLIENT)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.registerClient(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Role not found");
        }
    }

    // ============ REGISTER EMPLOYEE ============

    @Nested
    @DisplayName("registerEmployee()")
    class RegisterEmployeeTests {

        @Test
        @DisplayName("Should register employee successfully")
        void shouldRegisterEmployee() {
            RegisterRequestDto request = new RegisterRequestDto(
                    "Jane Employee", "jane@employee.com", "password123",
                    "IT", null
            );

            when(clientRepository.existsByEmail(anyString())).thenReturn(false);
            when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByName(RoleName.ROLE_EMPLOYEE)).thenReturn(Optional.of(employeeRole));
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(employee);
            when(jwtService.generateAccessToken(any())).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any())).thenReturn("refreshToken");
            when(jwtService.extractExpiration("accessToken")).thenReturn(new Date());

            AuthResponseDto result = authService.registerEmployee(request);

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo("accessToken");
            assertThat(result.user().name()).isEqualTo("Jane Employee");
            assertThat(result.user().roles()).contains("ROLE_EMPLOYEE");

            verify(roleRepository, times(1)).findByName(RoleName.ROLE_EMPLOYEE);
            verify(employeeRepository, times(1)).save(any(EmployeeEntity.class));
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email exists")
        void shouldThrowExceptionWhenEmailExists() {
            RegisterRequestDto request = new RegisterRequestDto(
                    "Jane Employee", "john@client.com", "password123",
                    "IT", null
            );

            when(clientRepository.existsByEmail("john@client.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerEmployee(request))
                    .isInstanceOf(DuplicateEmailException.class);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when role not found")
        void shouldThrowExceptionWhenRoleNotFound() {
            RegisterRequestDto request = new RegisterRequestDto(
                    "Jane Employee", "jane@employee.com", "password123",
                    "IT", null
            );

            when(clientRepository.existsByEmail(anyString())).thenReturn(false);
            when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
            when(roleRepository.findByName(RoleName.ROLE_EMPLOYEE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.registerEmployee(request))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    // ============ LOGIN ============

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("Should login client successfully")
        void shouldLoginClient() {
            AuthRequestDto request = new AuthRequestDto("john@client.com", "password123");
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(client);
            when(clientRepository.findByEmail("john@client.com")).thenReturn(Optional.of(client));
            when(jwtService.generateAccessToken(any())).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any())).thenReturn("refreshToken");
            when(jwtService.extractExpiration("accessToken")).thenReturn(new Date());

            AuthResponseDto result = authService.login(request);

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo("accessToken");
            assertThat(result.user().email()).isEqualTo("john@client.com");
            assertThat(result.user().roles()).contains("ROLE_CLIENT");

            verify(authenticationManager, times(1))
                    .authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Should login employee successfully")
        void shouldLoginEmployee() {
            AuthRequestDto request = new AuthRequestDto("jane@employee.com", "password123");
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(employee);
            when(clientRepository.findByEmail("jane@employee.com")).thenReturn(Optional.empty());
            when(employeeRepository.findByEmail("jane@employee.com")).thenReturn(Optional.of(employee));
            when(jwtService.generateAccessToken(any())).thenReturn("accessToken");
            when(jwtService.generateRefreshToken(any())).thenReturn("refreshToken");
            when(jwtService.extractExpiration("accessToken")).thenReturn(new Date());

            AuthResponseDto result = authService.login(request);

            assertThat(result).isNotNull();
            assertThat(result.user().email()).isEqualTo("jane@employee.com");
            assertThat(result.user().roles()).contains("ROLE_EMPLOYEE");
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when credentials are invalid")
        void shouldThrowExceptionWhenBadCredentials() {
            AuthRequestDto request = new AuthRequestDto("john@client.com", "wrongPassword");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user not found after auth")
        void shouldThrowExceptionWhenUserNotFound() {
            AuthRequestDto request = new AuthRequestDto("unknown@test.com", "password123");
            Authentication authentication = mock(Authentication.class);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(clientRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
            when(employeeRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }

    // ============ REFRESH ============

    @Nested
    @DisplayName("refresh()")
    class RefreshTests {

        @Test
        @DisplayName("Should refresh token for client")
        void shouldRefreshTokenForClient() {
            String refreshToken = "validRefreshToken";

            when(jwtService.extractEmail(refreshToken)).thenReturn("john@client.com");
            when(clientRepository.findByEmail("john@client.com")).thenReturn(Optional.of(client));
            when(jwtService.generateAccessToken(any())).thenReturn("newAccessToken");
            when(jwtService.generateRefreshToken(any())).thenReturn("newRefreshToken");
            when(jwtService.extractExpiration("newAccessToken")).thenReturn(new Date());

            AuthResponseDto result = authService.refresh(refreshToken);

            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo("newAccessToken");
            assertThat(result.refreshToken()).isEqualTo("newRefreshToken");
            assertThat(result.user().email()).isEqualTo("john@client.com");

            verify(jwtService, times(1)).extractEmail(refreshToken);
            verify(jwtService, times(1)).generateAccessToken(any());
            verify(jwtService, times(1)).generateRefreshToken(any());
        }

        @Test
        @DisplayName("Should refresh token for employee")
        void shouldRefreshTokenForEmployee() {
            String refreshToken = "validRefreshToken";

            when(jwtService.extractEmail(refreshToken)).thenReturn("jane@employee.com");
            when(clientRepository.findByEmail("jane@employee.com")).thenReturn(Optional.empty());
            when(employeeRepository.findByEmail("jane@employee.com")).thenReturn(Optional.of(employee));
            when(jwtService.generateAccessToken(any())).thenReturn("newAccessToken");
            when(jwtService.generateRefreshToken(any())).thenReturn("newRefreshToken");
            when(jwtService.extractExpiration("newAccessToken")).thenReturn(new Date());

            AuthResponseDto result = authService.refresh(refreshToken);

            assertThat(result).isNotNull();
            assertThat(result.user().email()).isEqualTo("jane@employee.com");
            assertThat(result.user().roles()).contains("ROLE_EMPLOYEE");
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            String refreshToken = "validRefreshToken";

            when(jwtService.extractEmail(refreshToken)).thenReturn("unknown@test.com");
            when(clientRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
            when(employeeRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.refresh(refreshToken))
                    .isInstanceOf(UsernameNotFoundException.class);
        }
    }
}