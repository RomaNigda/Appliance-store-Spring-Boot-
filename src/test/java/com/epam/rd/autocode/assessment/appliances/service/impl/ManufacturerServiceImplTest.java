package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ManufacturerNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ManufacturerMapper;
import com.epam.rd.autocode.assessment.appliances.repository.ManufacturerRepository;
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
@DisplayName("ManufacturerServiceImpl Tests")
class ManufacturerServiceImplTest {

    @Mock
    private ManufacturerRepository manufacturerRepository;

    @Mock
    private ManufacturerMapper manufacturerMapper;

    @InjectMocks
    private ManufacturerServiceImpl manufacturerService;

    private ManufacturerEntity entity;
    private ManufacturerRequestDto requestDto;
    private ManufacturerResponseDto responseDto;

    @BeforeEach
    void setUp() {
        entity = new ManufacturerEntity();
        entity.setId(1L);
        entity.setName("Samsung Electronics");

        requestDto = new ManufacturerRequestDto("Samsung Electronics");
        responseDto = new ManufacturerResponseDto(1L, "Samsung Electronics");
    }

    // ============ FIND ALL ============

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Should return page of manufacturers")
        void shouldReturnPageOfManufacturers() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ManufacturerEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

            when(manufacturerRepository.findAll(pageable)).thenReturn(page);
            when(manufacturerMapper.toResponseDto(entity)).thenReturn(responseDto);

            PageResponseDto<ManufacturerResponseDto> result = manufacturerService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("Samsung Electronics");
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isTrue();

            verify(manufacturerRepository, times(1)).findAll(pageable);
            verify(manufacturerMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should return empty page when no manufacturers")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ManufacturerEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(manufacturerRepository.findAll(pageable)).thenReturn(emptyPage);

            PageResponseDto<ManufacturerResponseDto> result = manufacturerService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
            assertThat(result.totalPages()).isEqualTo(0);

            verify(manufacturerRepository, times(1)).findAll(pageable);
            verifyNoInteractions(manufacturerMapper);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("Should create manufacturer successfully")
        void shouldCreateManufacturer() {
            when(manufacturerRepository.existsByName(anyString())).thenReturn(false);
            when(manufacturerMapper.toEntity(requestDto)).thenReturn(entity);
            when(manufacturerRepository.save(entity)).thenReturn(entity);
            when(manufacturerMapper.toResponseDto(entity)).thenReturn(responseDto);

            ManufacturerResponseDto result = manufacturerService.create(requestDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Samsung Electronics");

            verify(manufacturerRepository, times(1)).existsByName(anyString());
            verify(manufacturerMapper, times(1)).toEntity(requestDto);
            verify(manufacturerRepository, times(1)).save(entity);
            verify(manufacturerMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when duplicate name")
        void shouldThrowExceptionWhenDuplicate() {
            when(manufacturerRepository.existsByName(anyString())).thenReturn(true);

            assertThatThrownBy(() -> manufacturerService.create(requestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists")
                    .hasMessageContaining("Samsung Electronics");

            verify(manufacturerRepository, times(1)).existsByName(anyString());
            verify(manufacturerRepository, never()).save(any());
            verifyNoInteractions(manufacturerMapper);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Should return manufacturer by id")
        void shouldReturnManufacturerById() {
            when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(manufacturerMapper.toResponseDto(entity)).thenReturn(responseDto);

            ManufacturerResponseDto result = manufacturerService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Samsung Electronics");

            verify(manufacturerRepository, times(1)).findById(1L);
            verify(manufacturerMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should throw ManufacturerNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(manufacturerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> manufacturerService.findById(999L))
                    .isInstanceOf(ManufacturerNotFoundException.class)
                    .hasMessageContaining("999");

            verify(manufacturerRepository, times(1)).findById(999L);
            verifyNoInteractions(manufacturerMapper);
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("Should update manufacturer successfully")
        void shouldUpdateManufacturer() {
            ManufacturerRequestDto updateRequest = new ManufacturerRequestDto("Samsung Electronics Co.");
            ManufacturerResponseDto updatedResponse = new ManufacturerResponseDto(1L, "Samsung Electronics Co.");

            when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(manufacturerRepository.existsByName("Samsung Electronics Co.")).thenReturn(false);
            doNothing().when(manufacturerMapper).updateEntity(updateRequest, entity);
            when(manufacturerRepository.save(entity)).thenReturn(entity);
            when(manufacturerMapper.toResponseDto(entity)).thenReturn(updatedResponse);

            ManufacturerResponseDto result = manufacturerService.update(1L, updateRequest);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Samsung Electronics Co.");

            verify(manufacturerRepository, times(1)).findById(1L);
            verify(manufacturerMapper, times(1)).updateEntity(updateRequest, entity);
            verify(manufacturerRepository, times(1)).save(entity);
        }

        @Test
        @DisplayName("Should update manufacturer when name is the same")
        void shouldUpdateWhenNameIsSame() {
            // Той самий name — не перевіряємо existsByName
            when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(entity));
            doNothing().when(manufacturerMapper).updateEntity(requestDto, entity);
            when(manufacturerRepository.save(entity)).thenReturn(entity);
            when(manufacturerMapper.toResponseDto(entity)).thenReturn(responseDto);

            ManufacturerResponseDto result = manufacturerService.update(1L, requestDto);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Samsung Electronics");

            verify(manufacturerRepository, times(1)).findById(1L);
            verify(manufacturerRepository, never()).existsByName(anyString());
            verify(manufacturerMapper, times(1)).updateEntity(requestDto, entity);
        }

        @Test
        @DisplayName("Should throw ManufacturerNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(manufacturerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> manufacturerService.update(999L, requestDto))
                    .isInstanceOf(ManufacturerNotFoundException.class)
                    .hasMessageContaining("999");

            verify(manufacturerRepository, times(1)).findById(999L);
            verify(manufacturerRepository, never()).save(any());
            verifyNoInteractions(manufacturerMapper);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when new name already exists")
        void shouldThrowExceptionWhenNameExists() {
            ManufacturerRequestDto updateRequest = new ManufacturerRequestDto("Existing Name");

            when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(manufacturerRepository.existsByName("Existing Name")).thenReturn(true);

            assertThatThrownBy(() -> manufacturerService.update(1L, updateRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists")
                    .hasMessageContaining("Existing Name");

            verify(manufacturerRepository, times(1)).findById(1L);
            verify(manufacturerRepository, times(1)).existsByName("Existing Name");
            verify(manufacturerRepository, never()).save(any());
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Should delete manufacturer successfully")
        void shouldDeleteManufacturer() {
            when(manufacturerRepository.existsById(1L)).thenReturn(true);
            doNothing().when(manufacturerRepository).deleteById(1L);

            manufacturerService.delete(1L);

            verify(manufacturerRepository, times(1)).existsById(1L);
            verify(manufacturerRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ManufacturerNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(manufacturerRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> manufacturerService.delete(999L))
                    .isInstanceOf(ManufacturerNotFoundException.class)
                    .hasMessageContaining("999");

            verify(manufacturerRepository, times(1)).existsById(999L);
            verify(manufacturerRepository, never()).deleteById(anyLong());
        }
    }

    // ============ GET ENTITY BY ID ============

    @Nested
    @DisplayName("getManufacturerEntityById()")
    class GetEntityByIdTests {

        @Test
        @DisplayName("Should return entity by id")
        void shouldReturnEntityById() {
            when(manufacturerRepository.findById(1L)).thenReturn(Optional.of(entity));

            ManufacturerEntity result = manufacturerService.getManufacturerEntityById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Samsung Electronics");

            verify(manufacturerRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw ManufacturerNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(manufacturerRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> manufacturerService.getManufacturerEntityById(999L))
                    .isInstanceOf(ManufacturerNotFoundException.class)
                    .hasMessageContaining("999");

            verify(manufacturerRepository, times(1)).findById(999L);
        }
    }
}