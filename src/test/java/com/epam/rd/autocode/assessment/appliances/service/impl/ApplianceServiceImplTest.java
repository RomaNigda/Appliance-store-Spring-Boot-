package com.epam.rd.autocode.assessment.appliances.service.impl;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ApplianceEntity;
import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.mapper.ApplianceMapper;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.repository.ApplianceRepository;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplianceServiceImpl Tests")
class ApplianceServiceImplTest {

    @Mock
    private ApplianceRepository applianceRepository;

    @Mock
    private ApplianceMapper applianceMapper;

    @InjectMocks
    private ApplianceServiceImpl applianceService;

    private ApplianceEntity entity;
    private ApplianceRequestDto requestDto;
    private ApplianceResponseDto responseDto;
    private ManufacturerEntity manufacturerEntity;
    private ManufacturerResponseDto manufacturerDto;

    @BeforeEach
    void setUp() {
        manufacturerEntity = new ManufacturerEntity();
        manufacturerEntity.setId(1L);
        manufacturerEntity.setName("Samsung Electronics");

        manufacturerDto = new ManufacturerResponseDto(1L, "Samsung Electronics");

        entity = new ApplianceEntity();
        entity.setId(1L);
        entity.setName("Samsung Smart Fridge");
        entity.setCategory(Category.BIG);
        entity.setModel("RF28R7351SR");
        entity.setManufacturer(manufacturerEntity);
        entity.setPowerType(PowerType.AC220);
        entity.setCharacteristic("SmartThings compatible");
        entity.setDescription("Large capacity fridge");
        entity.setPower(600);
        entity.setPrice(new BigDecimal("1999.99"));

        requestDto = new ApplianceRequestDto(
                "Samsung Smart Fridge",
                Category.BIG,
                "RF28R7351SR",
                manufacturerEntity,
                PowerType.AC220,
                "SmartThings compatible",
                "Large capacity fridge",
                600,
                new BigDecimal("1999.99")
        );

        responseDto = new ApplianceResponseDto(
                1L,
                "Samsung Smart Fridge",
                Category.BIG,
                "RF28R7351SR",
                manufacturerDto,
                PowerType.AC220,
                "SmartThings compatible",
                "Large capacity fridge",
                600,
                new BigDecimal("1999.99")
        );
    }

    // ============ FIND ALL ============

    @Nested
    @DisplayName("findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Should return page of appliances")
        void shouldReturnPageOfAppliances() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ApplianceEntity> page = new PageImpl<>(List.of(entity), pageable, 1);

            when(applianceRepository.findAll(pageable)).thenReturn(page);
            when(applianceMapper.toResponseDto(entity)).thenReturn(responseDto);

            PageResponseDto<ApplianceResponseDto> result = applianceService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("Samsung Smart Fridge");
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.totalPages()).isEqualTo(1);
            assertThat(result.first()).isTrue();
            assertThat(result.last()).isTrue();

            verify(applianceRepository, times(1)).findAll(pageable);
            verify(applianceMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should return empty page when no appliances")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ApplianceEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(applianceRepository.findAll(pageable)).thenReturn(emptyPage);

            PageResponseDto<ApplianceResponseDto> result = applianceService.findAll(pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
            assertThat(result.totalPages()).isEqualTo(0);

            verify(applianceRepository, times(1)).findAll(pageable);
            verifyNoInteractions(applianceMapper);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Should return appliance by id")
        void shouldReturnApplianceById() {
            when(applianceRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(applianceMapper.toResponseDto(entity)).thenReturn(responseDto);

            ApplianceResponseDto result = applianceService.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Samsung Smart Fridge");
            assertThat(result.category()).isEqualTo(Category.BIG);
            assertThat(result.price()).isEqualByComparingTo(new BigDecimal("1999.99"));

            verify(applianceRepository, times(1)).findById(1L);
            verify(applianceMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should throw ApplianceNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(applianceRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applianceService.findById(999L))
                    .isInstanceOf(ApplianceNotFoundException.class)
                    .hasMessageContaining("999");

            verify(applianceRepository, times(1)).findById(999L);
            verifyNoInteractions(applianceMapper);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("Should create appliance successfully")
        void shouldCreateAppliance() {
            when(applianceRepository.existsByNameAndModel(anyString(), anyString())).thenReturn(false);
            when(applianceMapper.toEntity(requestDto)).thenReturn(entity);
            when(applianceRepository.save(entity)).thenReturn(entity);
            when(applianceMapper.toResponseDto(entity)).thenReturn(responseDto);

            ApplianceResponseDto result = applianceService.create(requestDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Samsung Smart Fridge");

            verify(applianceRepository, times(1)).existsByNameAndModel(anyString(), anyString());
            verify(applianceMapper, times(1)).toEntity(requestDto);
            verify(applianceRepository, times(1)).save(entity);
            verify(applianceMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when duplicate name and model")
        void shouldThrowExceptionWhenDuplicate() {
            when(applianceRepository.existsByNameAndModel(anyString(), anyString())).thenReturn(true);

            assertThatThrownBy(() -> applianceService.create(requestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already exists");

            verify(applianceRepository, times(1)).existsByNameAndModel(anyString(), anyString());
            verify(applianceRepository, never()).save(any());
            verifyNoInteractions(applianceMapper);
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("Should update appliance successfully")
        void shouldUpdateAppliance() {
            when(applianceRepository.findById(1L)).thenReturn(Optional.of(entity));
            doNothing().when(applianceMapper).updateEntity(requestDto, entity);
            when(applianceRepository.save(entity)).thenReturn(entity);
            when(applianceMapper.toResponseDto(entity)).thenReturn(responseDto);

            ApplianceResponseDto result = applianceService.update(1L, requestDto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Samsung Smart Fridge");

            verify(applianceRepository, times(1)).findById(1L);
            verify(applianceMapper, times(1)).updateEntity(requestDto, entity);
            verify(applianceRepository, times(1)).save(entity);
            verify(applianceMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should throw ApplianceNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(applianceRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applianceService.update(999L, requestDto))
                    .isInstanceOf(ApplianceNotFoundException.class)
                    .hasMessageContaining("999");

            verify(applianceRepository, times(1)).findById(999L);
            verify(applianceRepository, never()).save(any());
            verifyNoInteractions(applianceMapper);
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("Should delete appliance successfully")
        void shouldDeleteAppliance() {
            when(applianceRepository.existsById(1L)).thenReturn(true);
            doNothing().when(applianceRepository).deleteById(1L);

            applianceService.delete(1L);

            verify(applianceRepository, times(1)).existsById(1L);
            verify(applianceRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ApplianceNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(applianceRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> applianceService.delete(999L))
                    .isInstanceOf(ApplianceNotFoundException.class)
                    .hasMessageContaining("999");

            verify(applianceRepository, times(1)).existsById(999L);
            verify(applianceRepository, never()).deleteById(anyLong());
        }
    }

    // ============ GET ENTITY BY ID ============

    @Nested
    @DisplayName("getApplianceEntityById()")
    class GetEntityByIdTests {

        @Test
        @DisplayName("Should return entity by id")
        void shouldReturnEntityById() {
            when(applianceRepository.findById(1L)).thenReturn(Optional.of(entity));

            ApplianceEntity result = applianceService.getApplianceEntityById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Samsung Smart Fridge");

            verify(applianceRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw ApplianceNotFoundException when not found")
        void shouldThrowExceptionWhenNotFound() {
            when(applianceRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> applianceService.getApplianceEntityById(999L))
                    .isInstanceOf(ApplianceNotFoundException.class)
                    .hasMessageContaining("999");

            verify(applianceRepository, times(1)).findById(999L);
        }
    }

    // ============ CATEGORY STATISTICS ============

    @Nested
    @DisplayName("getCategoryStatistics()")
    class CategoryStatisticsTests {

        @Test
        @DisplayName("Should return category statistics")
        void shouldReturnCategoryStatistics() {
            List<Object[]> rawStats = List.of(
                    new Object[]{Category.BIG, 4L, 1949.74, new BigDecimal("799.99"), new BigDecimal("2499.99")},
                    new Object[]{Category.SMALL, 3L, 583.31, new BigDecimal("149.99"), new BigDecimal("1999.99")}
            );

            when(applianceRepository.getCategoryStatisticsRaw()).thenReturn(rawStats);

            List<CategoryStatsDto> result = applianceService.getCategoryStatistics();

            assertThat(result).hasSize(2);

            CategoryStatsDto bigStats = result.get(0);
            assertThat(bigStats.category()).isEqualTo(Category.BIG);
            assertThat(bigStats.count()).isEqualTo(4L);
            assertThat(bigStats.avgPrice()).isEqualByComparingTo(new BigDecimal("1949.74"));
            assertThat(bigStats.minPrice()).isEqualByComparingTo(new BigDecimal("799.99"));
            assertThat(bigStats.maxPrice()).isEqualByComparingTo(new BigDecimal("2499.99"));

            CategoryStatsDto smallStats = result.get(1);
            assertThat(smallStats.category()).isEqualTo(Category.SMALL);
            assertThat(smallStats.count()).isEqualTo(3L);

            verify(applianceRepository, times(1)).getCategoryStatisticsRaw();
        }

        @Test
        @DisplayName("Should return empty list when no stats")
        void shouldReturnEmptyList() {
            when(applianceRepository.getCategoryStatisticsRaw()).thenReturn(Collections.emptyList());

            List<CategoryStatsDto> result = applianceService.getCategoryStatistics();

            assertThat(result).isEmpty();

            verify(applianceRepository, times(1)).getCategoryStatisticsRaw();
        }
    }

    // ============ SEARCH BY MANUFACTURER AND PRICE ============

    @Nested
    @DisplayName("findByManufacturerAndPriceRange()")
    class SearchTests {

        @Test
        @DisplayName("Should return page of appliances by manufacturer and price")
        void shouldSearchByManufacturerAndPrice() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ApplianceEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
            BigDecimal minPrice = new BigDecimal("500");
            BigDecimal maxPrice = new BigDecimal("3000");

            when(applianceRepository.findByManufacturerAndPriceRange(
                    eq("Samsung Electronics"), eq(minPrice), eq(maxPrice), eq(pageable)
            )).thenReturn(page);
            when(applianceMapper.toResponseDto(entity)).thenReturn(responseDto);

            PageResponseDto<ApplianceResponseDto> result = applianceService
                    .findByManufacturerAndPriceRange("Samsung Electronics", minPrice, maxPrice, pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).name()).isEqualTo("Samsung Smart Fridge");
            assertThat(result.totalElements()).isEqualTo(1);

            verify(applianceRepository, times(1)).findByManufacturerAndPriceRange(
                    eq("Samsung Electronics"), eq(minPrice), eq(maxPrice), eq(pageable)
            );
            verify(applianceMapper, times(1)).toResponseDto(entity);
        }

        @Test
        @DisplayName("Should return empty page when no matches")
        void shouldReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 20);
            Page<ApplianceEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(applianceRepository.findByManufacturerAndPriceRange(
                    anyString(), any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)
            )).thenReturn(emptyPage);

            PageResponseDto<ApplianceResponseDto> result = applianceService
                    .findByManufacturerAndPriceRange("Unknown", BigDecimal.ZERO, BigDecimal.TEN, pageable);

            assertThat(result).isNotNull();
            assertThat(result.content()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);

            verify(applianceRepository, times(1)).findByManufacturerAndPriceRange(
                    anyString(), any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)
            );
            verifyNoInteractions(applianceMapper);
        }
    }
}