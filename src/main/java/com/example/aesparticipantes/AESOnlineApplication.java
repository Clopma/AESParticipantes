package com.example.aesparticipantes;

import com.example.aesparticipantes.Repositories.InscripcionRepository;
import com.example.aesparticipantes.Repositories.RefreshableRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableJpaRepositories(repositoryBaseClass = RefreshableRepositoryImpl.class)
@EnableCaching
public class AESOnlineApplication {


    @Autowired
    InscripcionRepository inscripcionRepository;

    public static void main(String[] args) {

        SpringApplication.run(AESOnlineApplication.class, args);


    }

    @PostConstruct
    public static void test() {





    }

}

