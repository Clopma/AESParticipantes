package com.example.aesparticipantes;

import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Utils.AESUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableCaching
public class AESOnlineApplication {


    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    InscripcionRepository inscripcionRepository;

    @Autowired
    ParticipanteRepository participanteRepository;

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    CompeticionRepository competicionRepository;

    public static void main(String[] args) {

        SpringApplication.run(AESOnlineApplication.class, args);


    }

    @PostConstruct
    public void test() throws IOException {


        try {
            for (final File fileEntry : new File("C:/users/clopm/desktop/mezclas").listFiles()) {
                if (!fileEntry.isDirectory()) {
                    //TODO ruta relativa
                    File nuevo = new File("C:/users/clopm/IdeaProjects/AESParticipantes/src/main/resources/static/img/mezclas/" + AESUtils.getHash(fileEntry.getName()) + ".png");
                    fileEntry.renameTo(nuevo);
                    new java.io.FileWriter(nuevo, true);
                }
            }
        } catch (Exception e) {

//Por si se me olvida comentarlo al subir a live
        }

    }

}

