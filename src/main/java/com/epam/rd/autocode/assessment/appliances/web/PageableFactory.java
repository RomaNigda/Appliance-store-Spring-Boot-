package com.epam.rd.autocode.assessment.appliances.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableFactory {

    private static final int DEFAULT_SIZE = 20;
    private static final int DEFAULT_OFFSET = 0;
    private static final int MAX_SIZE = 100;

    // Дозволені поля для сортування по кожній entity
    private static final Set<String> MANUFACTURER_SORT_FIELDS = Set.of("id", "name");
    private static final Set<String> APPLIANCE_SORT_FIELDS = Set.of("id", "name", "price", "model", "category", "powerType");
    private static final Set<String> ORDER_SORT_FIELDS = Set.of("id", "approved");
    private static final Set<String> CLIENT_SORT_FIELDS = Set.of("id", "name", "email");
    private static final Set<String> EMPLOYEE_SORT_FIELDS = Set.of("id", "name", "email", "department");

    private PageableFactory() {
    }

    // ============ MANUFACTURER ============
    public static Pageable forManufacturer(Integer page, Integer size, String sortBy, String sortDirection) {
        return create(page, size, sortBy, sortDirection, MANUFACTURER_SORT_FIELDS);
    }

    // ============ APPLIANCE ============
    public static Pageable forAppliance(Integer page, Integer size, String sortBy, String sortDirection) {
        return create(page, size, sortBy, sortDirection, APPLIANCE_SORT_FIELDS);
    }

    // ============ ORDER ============
    public static Pageable forOrder(Integer page, Integer size, String sortBy, String sortDirection) {
        return create(page, size, sortBy, sortDirection, ORDER_SORT_FIELDS);
    }

    // ============ CLIENT ============
    public static Pageable forClient(Integer page, Integer size, String sortBy, String sortDirection) {
        return create(page, size, sortBy, sortDirection, CLIENT_SORT_FIELDS);
    }

    // ============ EMPLOYEE ============
    public static Pageable forEmployee(Integer page, Integer size, String sortBy, String sortDirection) {
        return create(page, size, sortBy, sortDirection, EMPLOYEE_SORT_FIELDS);
    }

    // ============ DEFAULT (без сортування) ============
    public static Pageable create(Integer page, Integer size) {
        int pageNumber = page != null && page >= 0 ? page : DEFAULT_OFFSET;
        int pageSize = size != null && size > 0 ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;
        return PageRequest.of(pageNumber, pageSize);
    }

    // ============ PRIVATE HELPER ============
    private static Pageable create(Integer page, Integer size, String sortBy, String sortDirection, Set<String> allowedFields) {
        int pageNumber = page != null && page >= 0 ? page : DEFAULT_OFFSET;
        int pageSize = size != null && size > 0 ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;

        Sort sort = Sort.unsorted();

        if (sortBy != null && !sortBy.isBlank() && allowedFields.contains(sortBy)) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }

        return PageRequest.of(pageNumber, pageSize, sort);
    }
}