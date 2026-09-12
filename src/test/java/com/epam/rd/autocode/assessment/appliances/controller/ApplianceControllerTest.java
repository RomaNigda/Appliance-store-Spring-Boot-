package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.dto.ApplianceRequestDto;
import com.epam.rd.autocode.assessment.appliances.dto.ApplianceResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.CategoryStatsDto;
import com.epam.rd.autocode.assessment.appliances.dto.ManufacturerResponseDto;
import com.epam.rd.autocode.assessment.appliances.dto.PageResponseDto;
import com.epam.rd.autocode.assessment.appliances.entity.ManufacturerEntity;
import com.epam.rd.autocode.assessment.appliances.exceptions.ApplianceNotFoundException;
import com.epam.rd.autocode.assessment.appliances.model.Category;
import com.epam.rd.autocode.assessment.appliances.model.PowerType;
import com.epam.rd.autocode.assessment.appliances.service.ApplianceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ApplianceController Tests")
class ApplianceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplianceService applianceService;

    private ApplianceRequestDto requestDto;
    private ApplianceResponseDto responseDto;
    private ManufacturerResponseDto manufacturerDto;
    private ManufacturerEntity manufacturerEntity;

    @BeforeEach
    void setUp() {
        manufacturerDto = new ManufacturerResponseDto(1L, "Samsung Electronics");

        manufacturerEntity = new ManufacturerEntity();
        manufacturerEntity.setId(1L);
        manufacturerEntity.setName("Samsung Electronics");

        requestDto = new ApplianceRequestDto(
                "Samsung Smart Fridge",
                Category.BIG,
                "RF28R7351SR",
                manufacturerEntity,   // ← валідний manufacturer
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
    @DisplayName("GET /api/appliances")
    class FindAllTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return page of appliances")
        void shouldReturnPageOfAppliances() throws Exception {
            PageResponseDto<ApplianceResponseDto> pageResponse = new PageResponseDto<>(
                    List.of(responseDto), 0, 20, 1, 1, true, true
            );

            when(applianceService.findAll(any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/appliances")
                            .param("page", "0")
                            .param("size", "20")
                            .param("sortBy", "name")
                            .param("sortDirection", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].name").value("Samsung Smart Fridge"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(20))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(applianceService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return empty page when no appliances")
        void shouldReturnEmptyPage() throws Exception {
            PageResponseDto<ApplianceResponseDto> emptyPage = new PageResponseDto<>(
                    List.of(), 0, 20, 0, 0, true, true
            );

            when(applianceService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            mockMvc.perform(get("/api/appliances"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(applianceService, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return 401 when not authenticated")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/appliances"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(applianceService);
        }

        @Test
        @WithMockUser(roles = "CLIENT")
        @DisplayName("Should return 403 when user is CLIENT")
        void shouldReturn403WhenClient() throws Exception {
            mockMvc.perform(get("/api/appliances"))
                    .andExpect(status().isForbidden());

            verifyNoInteractions(applianceService);
        }
    }

    // ============ FIND BY ID ============

    @Nested
    @DisplayName("GET /api/appliances/{id}")
    class FindByIdTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return appliance by id")
        void shouldReturnApplianceById() throws Exception {
            when(applianceService.findById(1L)).thenReturn(responseDto);

            mockMvc.perform(get("/api/appliances/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Samsung Smart Fridge"))
                    .andExpect(jsonPath("$.category").value("BIG"))
                    .andExpect(jsonPath("$.model").value("RF28R7351SR"))
                    .andExpect(jsonPath("$.manufacturer.id").value(1L))
                    .andExpect(jsonPath("$.manufacturer.name").value("Samsung Electronics"))
                    .andExpect(jsonPath("$.powerType").value("AC220"))
                    .andExpect(jsonPath("$.price").value(1999.99));

            verify(applianceService, times(1)).findById(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when appliance not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(applianceService.findById(anyLong()))
                    .thenThrow(new ApplianceNotFoundException(999L));

            mockMvc.perform(get("/api/appliances/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(applianceService, times(1)).findById(999L);
        }
    }

    // ============ CREATE ============

    @Nested
    @DisplayName("POST /api/appliances")
    class CreateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should create appliance and return 201")
        void shouldCreateAppliance() throws Exception {
            when(applianceService.create(any(ApplianceRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(post("/api/appliances")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Samsung Smart Fridge"))
                    .andExpect(jsonPath("$.price").value(1999.99));

            verify(applianceService, times(1)).create(any(ApplianceRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when name is blank")
        void shouldReturn400WhenNameBlank() throws Exception {
            ApplianceRequestDto invalidRequest = new ApplianceRequestDto(
                    "", Category.BIG, "RF28R7351SR", manufacturerEntity, PowerType.AC220,
                    "char", "desc", 600, new BigDecimal("1999.99")
            );

            mockMvc.perform(post("/api/appliances")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());

            verifyNoInteractions(applianceService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when manufacturer is null")
        void shouldReturn400WhenManufacturerNull() throws Exception {
            ApplianceRequestDto invalidRequest = new ApplianceRequestDto(
                    "Name", Category.BIG, "RF28R7351SR", null, PowerType.AC220,
                    "char", "desc", 600, new BigDecimal("1999.99")
            );

            mockMvc.perform(post("/api/appliances")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.manufacturer").exists());

            verifyNoInteractions(applianceService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when category is null")
        void shouldReturn400WhenCategoryNull() throws Exception {
            ApplianceRequestDto invalidRequest = new ApplianceRequestDto(
                    "Name", null, "RF28R7351SR", manufacturerEntity, PowerType.AC220,
                    "char", "desc", 600, new BigDecimal("1999.99")
            );

            mockMvc.perform(post("/api/appliances")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.category").exists());

            verifyNoInteractions(applianceService);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 400 when price is negative")
        void shouldReturn400WhenPriceNegative() throws Exception {
            ApplianceRequestDto invalidRequest = new ApplianceRequestDto(
                    "Name", Category.BIG, "RF28R7351SR", manufacturerEntity, PowerType.AC220,
                    "char", "desc", 600, new BigDecimal("-100")
            );

            mockMvc.perform(post("/api/appliances")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.price").exists());

            verifyNoInteractions(applianceService);
        }
    }

    // ============ UPDATE ============

    @Nested
    @DisplayName("PUT /api/appliances/{id}")
    class UpdateTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should update appliance")
        void shouldUpdateAppliance() throws Exception {
            when(applianceService.update(eq(1L), any(ApplianceRequestDto.class))).thenReturn(responseDto);

            mockMvc.perform(put("/api/appliances/{id}", 1L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Samsung Smart Fridge"));

            verify(applianceService, times(1)).update(eq(1L), any(ApplianceRequestDto.class));
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when appliance not found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(applianceService.update(eq(999L), any(ApplianceRequestDto.class)))
                    .thenThrow(new ApplianceNotFoundException(999L));

            mockMvc.perform(put("/api/appliances/{id}", 999L)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isNotFound());

            verify(applianceService, times(1)).update(eq(999L), any(ApplianceRequestDto.class));
        }
    }

    // ============ DELETE ============

    @Nested
    @DisplayName("DELETE /api/appliances/{id}")
    class DeleteTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should delete appliance and return 204")
        void shouldDeleteAppliance() throws Exception {
            doNothing().when(applianceService).delete(1L);

            mockMvc.perform(delete("/api/appliances/{id}", 1L).with(csrf()))
                    .andExpect(status().isNoContent());

            verify(applianceService, times(1)).delete(1L);
        }

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return 404 when appliance not found")
        void shouldReturn404WhenNotFound() throws Exception {
            doThrow(new ApplianceNotFoundException(999L))
                    .when(applianceService).delete(999L);

            mockMvc.perform(delete("/api/appliances/{id}", 999L).with(csrf()))
                    .andExpect(status().isNotFound());

            verify(applianceService, times(1)).delete(999L);
        }
    }

    // ============ STATS ============

    @Nested
    @DisplayName("GET /api/appliances/stats/categories")
    class StatsTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should return category statistics")
        void shouldReturnCategoryStatistics() throws Exception {
            List<CategoryStatsDto> stats = List.of(
                    new CategoryStatsDto(Category.BIG, 4L,
                            new BigDecimal("1949.74"), new BigDecimal("799.99"), new BigDecimal("2499.99")),
                    new CategoryStatsDto(Category.SMALL, 3L,
                            new BigDecimal("583.31"), new BigDecimal("149.99"), new BigDecimal("1999.99"))
            );

            when(applianceService.getCategoryStatistics()).thenReturn(stats);

            mockMvc.perform(get("/api/appliances/stats/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].category").value("BIG"))
                    .andExpect(jsonPath("$[0].count").value(4))
                    .andExpect(jsonPath("$[1].category").value("SMALL"))
                    .andExpect(jsonPath("$[1].count").value(3));

            verify(applianceService, times(1)).getCategoryStatistics();
        }
    }

    // ============ SEARCH ============

    @Nested
    @DisplayName("GET /api/appliances/search")
    class SearchTests {

        @Test
        @WithMockUser(roles = "EMPLOYEE")
        @DisplayName("Should search appliances by manufacturer and price range")
        void shouldSearchByManufacturerAndPrice() throws Exception {
            PageResponseDto<ApplianceResponseDto> pageResponse = new PageResponseDto<>(
                    List.of(responseDto), 0, 20, 1, 1, true, true
            );

            when(applianceService.findByManufacturerAndPriceRange(
                    eq("Samsung Electronics"), eq(new BigDecimal("500")), eq(new BigDecimal("3000")),
                    any(Pageable.class)
            )).thenReturn(pageResponse);

            mockMvc.perform(get("/api/appliances/search")
                            .param("manufacturer", "Samsung Electronics")
                            .param("minPrice", "500")
                            .param("maxPrice", "3000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].name").value("Samsung Smart Fridge"));

            verify(applianceService, times(1)).findByManufacturerAndPriceRange(
                    eq("Samsung Electronics"), eq(new BigDecimal("500")), eq(new BigDecimal("3000")),
                    any(Pageable.class)
            );
        }
    }
}