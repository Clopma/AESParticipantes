package com.example.aesparticipantes;

import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

import javax.annotation.PostConstruct;
import java.io.IOException;

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
    public void test() throws IOException {

//
//            for (final File fileEntry : new File("C:/users/clopm/desktop/mezclas").listFiles()) {
//                if (!fileEntry.isDirectory()) {
//                    //TODO ruta relativa
//                    File nuevo = new File("C:/users/clopm/IdeaProjects/AESParticipantes/src/main/resources/static/img/mezclas/" + AESUtils.getHash(fileEntry.getName()) + ".png");
//                    fileEntry.renameTo(nuevo);
//                    new java.io.FileWriter(nuevo, true );
//                }
//            }

    }

}

