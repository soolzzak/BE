package com.example.zzan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ZzanApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZzanApplication.class, args);
    }

}
