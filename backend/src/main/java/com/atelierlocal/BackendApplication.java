package com.atelierlocal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.atelierlocal.repository")
public class BackendApplication {
    public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
    }
}
