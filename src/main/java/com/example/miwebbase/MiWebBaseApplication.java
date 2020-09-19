package com.example.miwebbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableCaching
public class MiWebBaseApplication {

    public static void main(String[] args) {

        SpringApplication.run(MiWebBaseApplication.class, args);

        //TODO: Schedule limpiar caché

    }

}
