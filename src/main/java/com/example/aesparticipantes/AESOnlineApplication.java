package com.example.aesparticipantes;

import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

import javax.annotation.PostConstruct;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableCaching
public class AESOnlineApplication {


    @Autowired
    EventoRepository eventoRepository;


    @Autowired
    CompeticionRepository competicionRepository;

    public static void main(String[] args) {

        SpringApplication.run(AESOnlineApplication.class, args);


    }

    @PostConstruct
    public void test() {

//        Competicion competicion = competicionRepository.findByNombre("Nacionline 2020");
//        Evento evento = eventoRepository.getEventoPorCategoriaYNombre("3x3x3", "Nacionline 2020");
//        int a = evento.getCompeticion().getJornadas().size; //DEBERIA SER 5

    }

}

