package com.ghoomi;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GhoomiApplication {

    @PostConstruct
    void init() {
        // Enforce application-wide UTC timezone contract as defined in TRD & Backend Schema
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(GhoomiApplication.class, args);
    }
}

