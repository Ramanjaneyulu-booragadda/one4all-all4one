package com.newbusiness.one4all.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DuplicatePaymentException extends RuntimeException {
    public DuplicatePaymentException(String message) {
        super(message);
        log.error("DuplicatePaymentException: {}", message);
    }
}
