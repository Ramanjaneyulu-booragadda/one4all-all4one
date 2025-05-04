package com.newbusiness.one4all.util;

import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.Map;

public class PaginationUtils {

    private PaginationUtils() {
        // Utility class, private constructor
    }

    public static <T> Map<String, Object> preparePaginationResponse(Page<T> pageResult) {
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