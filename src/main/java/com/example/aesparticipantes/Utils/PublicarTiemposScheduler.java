package com.example.aesparticipantes.Utils;

import com.example.aesparticipantes.Controllers.CacheController;
import com.example.aesparticipantes.Entities.Jornada;
import com.example.aesparticipantes.Repositories.JornadaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
@EnableScheduling
public class PublicarTiemposScheduler implements SchedulingConfigurer {

    @Autowired
    Environment env;

    @Autowired
    JornadaRepository jornadaRepository;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    CacheController cacheController;

    Logger logger = LoggerFactory.getLogger(PublicarTiemposScheduler.class);

    @Bean(destroyMethod = "shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());

        AtomicReference<Jornada> jornadaAPublicar = new AtomicReference<>();
        taskRegistrar.addTriggerTask(
                () -> {
                        cacheController.limpiar();
                        logger.info("Caché limpiada desde job");
                    // Descartado, pero funcionaba: mejor limpiarla toda, por prevención de dejarme algunas cachés, y de paso limpieza de memoria

                    // jornadaAPublicar.get().getCompeticion().getEventos().forEach(e -> {
                    //     cacheManager.getCache("rankingsGlobales").evict(Evento.getEventoId(e.getCompeticion(), e.getCategoria()));
                    //     cacheManager.getCache("rankingsJornada").evict(Evento.getEventoId(e.getCompeticion(), e.getCategoria()) + "-" + jornadaAPublicar.get().getNumeroJornada());
                    //     // Cachear también los participantes en la competición y quizá más cosas
                    // });

                },
                triggerContext -> {

                    List<Jornada> jornadasPorFinalizar = jornadaRepository.getProximasJornadasEnFinalizar();

                    if(jornadasPorFinalizar.size() == 0){
                        Calendar nextExecutionTime = Calendar.getInstance();
                        nextExecutionTime.add(Calendar.HOUR, 12);
                        return nextExecutionTime.getTime();
                    } else {
                        Jornada jornada = jornadasPorFinalizar.get(0);
                        jornadaAPublicar.set(jornada);
                        logger.info(String.format("Próxima ejecución de limpieza de caché programada para: %s", jornada.getFechaFin()));
                        return jornada.getFechaFin();

                    }

                }
        );
    }
}