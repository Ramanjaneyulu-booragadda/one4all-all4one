package com.newbusiness.one4all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class One4allAll4oneApplication {
    private static final Logger logger = LoggerFactory.getLogger(One4allAll4oneApplication.class);

    public static void main(String[] args) {
        logger.info("Starting One4allAll4oneApplication...");
        try {
            SpringApplication.run(One4allAll4oneApplication.class, args);
            logger.info("Application started successfully.");
        } catch (Exception e) {
            logger.error("Application failed to start.", e);
            throw e;
        }
    }
}
