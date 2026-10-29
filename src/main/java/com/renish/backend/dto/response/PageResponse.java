
package com.renish.backend.dto.response;

import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        boolean empty,
        boolean first,
        boolean last,
        int number,
        int numberOfElements,
        PageableResponse pageable,
        int size,
        SortResponse sort,
        long totalElements,
        int totalPages
) {

    /**
     * Bean-style accessor for the page content. The record accessor is {@code content()},
     * this one exists so callers (and tests) written against Spring's {@code Page#getContent()}
     * keep working after switching to {@link PageResponse}.
     */
    public List<T> getContent() {
        return content;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        Pageable pageable = page.getPageable();
        boolean paged = pageable.isPaged();
        Sort sort = page.getSort();

        SortResponse sortResponse = SortResponse.builder()
                .empty(sort.isEmpty())
                .sorted(sort.isSorted())
                .unsorted(sort.isUnsorted())
                .build();

        // Pageable.unpaged() throws on pageNumber/offset, so those are only read for a paged request
        PageableResponse pageableResponse = PageableResponse.builder()
                .offset(paged ? pageable.getOffset() : 0L)
                .pageNumber(paged ? pageable.getPageNumber() : 0)
                .pageSize(page.getSize())
                .paged(paged)
                .sort(sortResponse)
                .unpaged(!paged)
                .build();

        return PageResponse.<T>builder()
                .content(page.getContent())
                .empty(page.isEmpty())
                .first(page.isFirst())
                .last(page.isLast())
                .number(page.getNumber())
                .numberOfElements(page.getNumberOfElements())
                .pageable(pageableResponse)
                .size(page.getSize())
                .sort(sortResponse)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Builder
    public record PageableResponse(
            long offset,
            int pageNumber,
            int pageSize,
            boolean paged,
            SortResponse sort,
            boolean unpaged
    ) {
    }

    @Builder
    public record SortResponse(
            boolean empty,
            boolean sorted,
            boolean unsorted
    ) {
    }
}
