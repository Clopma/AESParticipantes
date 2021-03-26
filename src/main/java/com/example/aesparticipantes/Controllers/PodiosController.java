package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.*;
import com.example.aesparticipantes.Repositories.ClasificadoRepository;
import com.example.aesparticipantes.Repositories.CompeticionRepository;
import com.example.aesparticipantes.Repositories.TemporadaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PodiosController {


    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    TemporadaRepository temporadaRepository;


    @RequestMapping("/podios/{nombre}")
    public String getPodios(Model model, @PathVariable("nombre") String nombre) {

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombre);
        Optional<Temporada> temporada = temporadaRepository.findByNombre(nombre);

        if(!competicion.isPresent() && !temporada.isPresent()){
            //TODO: Añadir mensaje y log
            return "error/404";
        }

        List<Object[]> medallas = competicion.isPresent() ?
                clasificadoRepository.getMedallasCompeticion(competicion.get().getNombre()) :
                clasificadoRepository.getMedallasTemporada(temporada.get().getNombre());

        Map<Categoria, List<ClasificadoRepository.Medalla>> podios = medallas.stream().map(m ->

                ClasificadoRepository.Medalla.builder()
                        .categoria((Categoria) m[0])
                        .participante((Participante) m[1])
                        .ronda((String) m[2])
                        .build()
        ).collect(Collectors.groupingBy(ClasificadoRepository.Medalla::getCategoria, LinkedHashMap::new, Collectors.toList()));

        podios = podios.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ordenarMedallas(e.getValue()), (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new));

        model.addAttribute("isCompeticion", competicion.isPresent());
        model.addAttribute("nombre", competicion.isPresent() ? competicion.get().getNombre() : temporada.get().getNombre());
        model.addAttribute("podios", podios);

        return "podios";
    }


    private List<ClasificadoRepository.Medalla> ordenarMedallas(List<ClasificadoRepository.Medalla> l) {

        List<ClasificadoRepository.Medalla> medallasOrdenadas = new ArrayList<>();
        medallasOrdenadas.addAll(l.stream().filter(m -> m.getRonda().equals(Clasificado.NombreRonda.MEDALLA_ORO.name())).collect(Collectors.toList()));
        medallasOrdenadas.addAll(l.stream().filter(m -> m.getRonda().equals(Clasificado.NombreRonda.MEDALLA_PLATA.name())).collect(Collectors.toList()));
        medallasOrdenadas.addAll(l.stream().filter(m -> m.getRonda().equals(Clasificado.NombreRonda.MEDALLA_BRONCE.name())).collect(Collectors.toList()));

        return medallasOrdenadas;
    }

}
