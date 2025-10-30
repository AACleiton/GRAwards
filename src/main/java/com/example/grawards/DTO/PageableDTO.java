package com.example.grawards.DTO;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

public class PageableDTO {

    private int page;
    private int size;
    private String sort;
    private Sort.Direction direction;

    public PageableDTO() {
    }

    public PageableDTO(int page, int size, String sort, Sort.Direction direction) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.direction = direction;
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Sort.Direction getDirection() {
        return direction;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public Pageable toPageable() {
        if(isNull(sort)) {
            return PageRequest.of(page, size);
        }

        var directionValid = ofNullable(direction).orElse(Sort.Direction.ASC);

        return PageRequest.of(page, size, Sort.by(new Sort.Order(directionValid, sort)));
    }
}
