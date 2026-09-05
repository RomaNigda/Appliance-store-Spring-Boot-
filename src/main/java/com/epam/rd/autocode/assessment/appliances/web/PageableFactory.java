package com.epam.rd.autocode.assessment.appliances.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public final class PageableFactory {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("lastName", "hired", "position", "salary");
    private static final int DEFAULT_SIZE = 20;

    private PageableFactory() {
    }

    public static Pageable create(Integer page, Integer size, String sort) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : DEFAULT_SIZE;

        if (sort == null || sort.isBlank()) {
            return PageRequest.of(pageNumber, pageSize);
        }
        if (!ALLOWED_SORT_FIELDS.contains(sort)) {
            throw new IllegalArgumentException(
                    "Unsupported sort field '" + sort + "'. Allowed: " + ALLOWED_SORT_FIELDS);
        }
        return PageRequest.of(pageNumber, pageSize, Sort.by(sort).ascending());
    }
}