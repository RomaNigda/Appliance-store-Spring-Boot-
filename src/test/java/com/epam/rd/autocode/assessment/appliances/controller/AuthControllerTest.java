package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.AuthRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.AuthResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.RegisterRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.UserInfoDto;
import com.epam.rd.autocode.assessment.appliances.exceptions.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.service.impl.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private AuthResponseDto authResponse;
    private UserInfoDto userInfo;

    @BeforeEach
    void setUp() {
        userInfo = new UserInfoDto(
                1L,
                "John Client",
                "john@client.com",
                Set.of("ROLE_CLIENT")
        );

        authResponse = new AuthResponseDto(
                "accessToken123",
                "refreshToken123",
                "Bearer",
                86400000L,
                userInfo
        );
    }

    // ============ REGISTER CLIENT ============

    @Nested
    @DisplayName("POST /api/auth/register/client")
    class RegisterClientTests {

        @Test
        @DisplayName("Should register client and return 201")
        void shouldRegisterClient() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "john@client.com", "password123",
                    null, "1234-5678-9012-3456"
            );

            when(authService.registerClient(any(RegisterRequestDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/api/auth/register/client")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                    .andExpect(jsonPath("$.refreshToken").value("refreshToken123"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresIn").value(86400000L))
                    .andExpect(jsonPath("$.user.id").value(1L))
                    .andExpect(jsonPath("$.user.name").value("John Client"))
                    .andExpect(jsonPath("$.user.email").value("john@client.com"))
                    .andExpect(jsonPath("$.user.roles[0]").value("ROLE_CLIENT"));

            verify(authService, times(1)).registerClient(any(RegisterRequestDto.class));
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenDuplicateEmail() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "john@client.com", "password123",
                    null, "1234-5678-9012-3456"
            );

            when(authService.registerClient(any(RegisterRequestDto.class)))
                    .thenThrow(new DuplicateEmailException("Email already registered: john@client.com"));

            mockMvc.perform(post("/api/auth/register/client")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "invalid-email", "password123",
                    null, "1234-5678-9012-3456"
            );

            mockMvc.perform(post("/api/auth/register/client")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "", "john@client.com", "password123",
                    null, "1234-5678-9012-3456"
            );

            mockMvc.perform(post("/api/auth/register/client")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("Should return 400 when password is too short")
        void shouldReturn400WhenPasswordShort() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "John Client", "john@client.com", "123",
                    null, "1234-5678-9012-3456"
            );

            mockMvc.perform(post("/api/auth/register/client")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());

            verifyNoInteractions(authService);
        }
    }

    // ============ REGISTER EMPLOYEE ============

    @Nested
    @DisplayName("POST /api/auth/register/employee")
    class RegisterEmployeeTests {

        @Test
        @DisplayName("Should register employee and return 201")
        void shouldRegisterEmployee() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "Jane Employee", "jane@employee.com", "password123",
                    "IT", null
            );

            UserInfoDto employeeInfo = new UserInfoDto(
                    3L, "Jane Employee", "jane@employee.com", Set.of("ROLE_EMPLOYEE")
            );
            AuthResponseDto employeeResponse = new AuthResponseDto(
                    "accessToken123", "refreshToken123", "Bearer", 86400000L, employeeInfo
            );

            when(authService.registerEmployee(any(RegisterRequestDto.class))).thenReturn(employeeResponse);

            mockMvc.perform(post("/api/auth/register/employee")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                    .andExpect(jsonPath("$.user.name").value("Jane Employee"))
                    .andExpect(jsonPath("$.user.roles[0]").value("ROLE_EMPLOYEE"));

            verify(authService, times(1)).registerEmployee(any(RegisterRequestDto.class));
        }

        @Test
        @DisplayName("Should return 409 when email already exists")
        void shouldReturn409WhenDuplicateEmail() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "Jane Employee", "jane@employee.com", "password123",
                    "IT", null
            );

            when(authService.registerEmployee(any(RegisterRequestDto.class)))
                    .thenThrow(new DuplicateEmailException("Email already registered"));

            mockMvc.perform(post("/api/auth/register/employee")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            RegisterRequestDto request = new RegisterRequestDto(
                    "Jane Employee", "not-an-email", "password123",
                    "IT", null
            );

            mockMvc.perform(post("/api/auth/register/employee")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authService);
        }
    }

    // ============ LOGIN ============

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully and return 200")
        void shouldLoginSuccessfully() throws Exception {
            AuthRequestDto request = new AuthRequestDto("john@client.com", "password123");

            when(authService.login(any(AuthRequestDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                    .andExpect(jsonPath("$.refreshToken").value("refreshToken123"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.user.email").value("john@client.com"));

            verify(authService, times(1)).login(any(AuthRequestDto.class));
        }

        @Test
        @DisplayName("Should return 401 when credentials are invalid")
        void shouldReturn401WhenBadCredentials() throws Exception {
            AuthRequestDto request = new AuthRequestDto("john@client.com", "wrongPassword");

            when(authService.login(any(AuthRequestDto.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 400 when email is blank")
        void shouldReturn400WhenEmailBlank() throws Exception {
            AuthRequestDto request = new AuthRequestDto("", "password123");

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            AuthRequestDto request = new AuthRequestDto("invalid-email", "password123");

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.email").exists());

            verifyNoInteractions(authService);
        }

        @Test
        @DisplayName("Should return 400 when password is blank")
        void shouldReturn400WhenPasswordBlank() throws Exception {
            AuthRequestDto request = new AuthRequestDto("john@client.com", "");

            mockMvc.perform(post("/api/auth/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.password").exists());

            verifyNoInteractions(authService);
        }
    }

    // ============ REFRESH ============

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class RefreshTests {

        @Test
        @DisplayName("Should refresh token and return 200")
        void shouldRefreshToken() throws Exception {
            String refreshToken = "validRefreshToken";

            when(authService.refresh(anyString())).thenReturn(authResponse);

            mockMvc.perform(post("/api/auth/refresh")
                            .with(csrf())
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(refreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                    .andExpect(jsonPath("$.refreshToken").value("refreshToken123"))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"));

            verify(authService, times(1)).refresh(refreshToken);
        }

        @Test
        @DisplayName("Should return 400 when refresh token is blank")
        void shouldReturn400WhenRefreshTokenBlank() throws Exception {
            mockMvc.perform(post("/api/auth/refresh")
                            .with(csrf())
                            .contentType(MediaType.TEXT_PLAIN)
                            .content(""))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(authService);
        }
    }
}