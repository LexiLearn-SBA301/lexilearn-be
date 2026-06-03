package com.sba.lexilearnbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LexilearnBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LexilearnBeApplication.class, args);
    }

}
