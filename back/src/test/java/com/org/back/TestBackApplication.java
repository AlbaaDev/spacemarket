package com.org.back;

import org.springframework.boot.SpringApplication;

import com.org.back.support.TestcontainersConfiguration;

/**
 * Runs the backend against a throwaway PostgreSQL in Docker, filled with the demo data of data.sql.
 * The only prerequisite is Docker: run it from the IDE, or with {@code mvn spring-boot:test-run}.
 */
public class TestBackApplication {

    public static void main(String[] args) {
        SpringApplication.from(BackApplication::main)
            .with(TestcontainersConfiguration.class)
            .withAdditionalProfiles("demo")
            .run(args);
    }
}
