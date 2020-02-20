package com.excursions.excursions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ExcursionsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExcursionsApplication.class, args);
    }
}
