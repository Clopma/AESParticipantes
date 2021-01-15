package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Temporada;
import com.example.aesparticipantes.Models.TimelinePointDivisiones;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.TemporadaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class TemporadaController {

    private static final String FRASE_HACKER = "Esto no debería ocurrir a no ser que seas un hacker.";

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    TemporadaRepository temporadaRepository;

    Logger logger = LoggerFactory.getLogger(TemporadaController.class);

    @GetMapping("/temporada/{nombreTemporada}")
    public String elegirCategoria(@PathVariable("nombreTemporada") String nombreTemporada, Model model) {

        Optional<Temporada> temporada = temporadaRepository.findByNombre(nombreTemporada);

        if (!temporada.isPresent()){
            model.addAttribute("mensaje", "No hay ninguna temporada llamada "+ nombreTemporada +".");
            logger.warn("404 con mensaje: " + Objects.requireNonNull(model.getAttribute("mensaje")).toString());
            return "error/404";
        }

        List<TimelinePointDivisiones> timeline = TimelinePointDivisiones.getTimelineCompleta(temporada.get());
        model.addAttribute("nombreTemporada", temporada.get().getNombre());
        //TODO: Convertir a objeto de vista sin entidades
        model.addAttribute("clasificaciones", temporada.get().getClasificaciones());
        model.addAttribute("timeline", timeline);
        return "temporada";
    }


}
