package com.newbusiness.one4all.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PaginationUtils {

    private PaginationUtils() {
        // Utility class, private constructor
    }

    public static <T> Map<String, Object> preparePaginationResponse(Page<T> pageResult) {
        log.info("Preparing pagination response: page={}, size={}, totalElements={}", pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements());
        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", pageResult.getNumber());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("pageSize", pageResult.getSize());
        response.put("isLastPage", pageResult.isLast());
        response.put("content", pageResult.getContent());
        return response;
    }
}