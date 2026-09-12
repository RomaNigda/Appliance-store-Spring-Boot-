package com.epam.rd.autocode.assessment.appliances.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResponseDto<T>(

        @NotNull(message = "{page.content.notNull}")
        @Valid
        List<T> content,

        @PositiveOrZero(message = "{page.page.positiveOrZero}")
        int page,

        @PositiveOrZero(message = "{page.size.positiveOrZero}")
        int size,

        @PositiveOrZero(message = "{page.totalElements.positiveOrZero}")
        long totalElements,

        @PositiveOrZero(message = "{page.totalPages.positiveOrZero}")
        int totalPages,

        boolean first,

        boolean last
) {
    public static <E, D> PageResponseDto<D> from(Page<E> page, Function<E, D> mapper) {
        return new PageResponseDto<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}