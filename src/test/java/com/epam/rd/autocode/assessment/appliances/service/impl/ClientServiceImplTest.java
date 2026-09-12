package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ClientRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ClientResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ClientEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ClientNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ClientMapper;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientServiceImpl Tests")
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientServiceImpl clientService;

    private ClientEntity client;
    private ClientRequestDto requestDto;
    private ClientResponseDto responseDto;

    @BeforeEach
    void setUp() {
        client = new ClientEntity();
        client.setId(1L);
        client.setName("John Client");
        client.setEmail("john@client.com");
        client.setPassword("encodedPassword");
        client.setCard("1234-5678-9012-3456");

        requestDto = new ClientRequestDto(
                "John Client",
                "john@client.com",
                "password123",
                "1234-5678-9012-3456"
        );

        responseDto = new ClientResponseDto(
                1L,
                "John Client",
                "john@client.com",
                "encodedPassword",
                "1234-5678-9012-3456"
        );
    }

    // ============ FIND ALL ============

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Should return page of clients")
        void shouldReturnPageOfClients() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ClientEntity> page = new PageImpl<>(List.of(client), pageable, 1);

            when(clientRepository.findAll(pageable)).thenReturn(page);
            when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

            PageResponseDto<ClientResponseDto> result = clientService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("John Client");
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isTrue();

            verify(clientRepository, times(1)).findAll(pageable);
            verify(clientMapper, times(1)).toResponseDto(client);
        }

        @Test
        @DisplayName("Should return empty page when no clients")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ClientEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(clientRepository.findAll(pageable)).thenReturn(emptyPage);

            PageResponseDto<ClientResponseDto> result = clientService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);

            verify(clientRepository, times(1)).findAll(pageable);
            verifyNoInteractions(clientMapper);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Should return client by id")
        void shouldReturnClientById() {
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

            ClientResponseDto result = clientService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("John Client");
            assertThat(result.email()).isEqualTo("john@client.com");

            verify(clientRepository, times(1)).findById(1L);
            verify(clientMapper, times(1)).toResponseDto(client);
        }

        @Test
        @DisplayName("Should throw ClientNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(clientRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientService.findById(999L))
                    .isInstanceOf(ClientNotFoundException.class)
                    .hasMessageContaining("999");

            verify(clientRepository, times(1)).findById(999L);
            verifyNoInteractions(clientMapper);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("Should create client successfully")
        void shouldCreateClient() {
            when(clientRepository.existsByEmail(anyString())).thenReturn(false);
            when(clientMapper.toEntity(requestDto)).thenReturn(client);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

            ClientResponseDto result = clientService.create(requestDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("John Client");

            verify(clientRepository, times(1)).existsByEmail(anyString());
            verify(clientMapper, times(1)).toEntity(requestDto);
            verify(passwordEncoder, times(1)).encode("password123");
            verify(clientRepository, times(1)).save(client);
            verify(clientMapper, times(1)).toResponseDto(client);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when duplicate email")
        void shouldThrowExceptionWhenDuplicate() {
            when(clientRepository.existsByEmail(anyString())).thenReturn(true);

            assertThatThrownBy(() -> clientService.create(requestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists")
                    .hasMessageContaining("john@client.com");

            verify(clientRepository, times(1)).existsByEmail(anyString());
            verify(clientRepository, never()).save(any());
            verifyNoInteractions(clientMapper);
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("Should update client successfully with same password")
        void shouldUpdateClientWithSamePassword() {
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

            ClientResponseDto result = clientService.update(1L, requestDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);

            verify(clientRepository, times(1)).findById(1L);
            verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
            verify(passwordEncoder, never()).encode(anyString());
            verify(clientRepository, times(1)).save(client);
        }

        @Test
        @DisplayName("Should update client and encode new password when changed")
        void shouldUpdateClientWithNewPassword() {
            ClientRequestDto updateRequest = new ClientRequestDto(
                    "John Client",
                    "john@client.com",
                    "newPassword123",
                    "1234-5678-9012-3456"
            );

            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(passwordEncoder.matches("newPassword123", "encodedPassword")).thenReturn(false);
            when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

            ClientResponseDto result = clientService.update(1L, updateRequest);

            assertThat(result).isNotNull();

            verify(passwordEncoder, times(1)).matches("newPassword123", "encodedPassword");
            verify(passwordEncoder, times(1)).encode("newPassword123");
            verify(clientRepository, times(1)).save(client);
        }

        @Test
        @DisplayName("Should update client when email is the same")
        void shouldUpdateWhenEmailIsSame() {
            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(clientRepository.save(client)).thenReturn(client);
            when(clientMapper.toResponseDto(client)).thenReturn(responseDto);

            ClientResponseDto result = clientService.update(1L, requestDto);

            assertThat(result).isNotNull();

            verify(clientRepository, times(1)).findById(1L);
            // Email такий самий — не перевіряємо existsByEmail
            verify(clientRepository, never()).existsByEmail(anyString());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when new email already exists")
        void shouldThrowExceptionWhenEmailExists() {
            ClientRequestDto updateRequest = new ClientRequestDto(
                    "John Client",
                    "existing@client.com",
                    "password123",
                    "1234-5678-9012-3456"
            );

            when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
            when(clientRepository.existsByEmail("existing@client.com")).thenReturn(true);

            assertThatThrownBy(() -> clientService.update(1L, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");

            verify(clientRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ClientNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(clientRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientService.update(999L, requestDto))
                    .isInstanceOf(ClientNotFoundException.class)
                    .hasMessageContaining("999");

            verify(clientRepository, times(1)).findById(999L);
            verify(clientRepository, never()).save(any());
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Should delete client successfully")
        void shouldDeleteClient() {
            when(clientRepository.existsById(1L)).thenReturn(true);
            doNothing().when(clientRepository).deleteById(1L);

            clientService.delete(1L);

            verify(clientRepository, times(1)).existsById(1L);
            verify(clientRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ClientNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(clientRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> clientService.delete(999L))
                    .isInstanceOf(ClientNotFoundException.class)
                    .hasMessageContaining("999");

            verify(clientRepository, times(1)).existsById(999L);
            verify(clientRepository, never()).deleteById(anyLong());
        }
    }
}