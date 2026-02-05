package com.zalmuk.swwim.api.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이징 응답 래퍼
 */
@Schema(description = "페이징 응답")
public class PageResponse<T> {

    @Schema(description = "데이터 목록")
    private List<T> content;

    @Schema(description = "현재 페이지 번호 (0부터 시작)")
    private int page;

    @Schema(description = "페이지 크기")
    private int size;

    @Schema(description = "전체 요소 개수")
    private long totalElements;

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "첫 번째 페이지 여부")
    private boolean first;

    @Schema(description = "마지막 페이지 여부")
    private boolean last;

    @Schema(description = "비어있는지 여부")
    private boolean empty;

    // Constructors
    public PageResponse() {
    }

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }

    public PageResponse(List<T> content, Page<?> page) {
        this.content = content;
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }

    // Static factory methods
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page);
    }

    public static <T, E> PageResponse<T> of(List<T> content, Page<E> page) {
        return new PageResponse<>(content, page);
    }

    // Getters and Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
