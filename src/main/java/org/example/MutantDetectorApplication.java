package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Mutant Detector API - Spring Boot Application
 * API REST para detectar mutantes analizando secuencias de ADN.
 */
@SpringBootApplication
public class MutantDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MutantDetectorApplication.class, args);
    }
}
