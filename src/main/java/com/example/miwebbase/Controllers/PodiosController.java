package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Clasificado;
import com.example.miwebbase.Entities.Competicion;
import com.example.miwebbase.Entities.Participante;
import com.example.miwebbase.repositories.ClasificadoRepository;
import com.example.miwebbase.repositories.CompeticionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PodiosController {


    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    CompeticionRepository competicionRepository;

    @RequestMapping("/podios/{nombreCompeticion}")
    public String getPodios(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion) {

        Competicion competicion = competicionRepository.findByNombre(nombreCompeticion);

        Map<Categoria, List<ClasificadoRepository.Medalla>> podios = clasificadoRepository.getMedallas(competicion.getNombre()).stream().map(m ->

               ClasificadoRepository.Medalla.builder()
                .categoria((Categoria) m[0])
                .participante((Participante) m[1])
                .ronda((String) m[2]).build()
        ).collect(Collectors.groupingBy(ClasificadoRepository.Medalla::getCategoria, LinkedHashMap::new, Collectors.toList()));

        podios = podios.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ordenarMedallas(e.getValue()), (u, v) -> {throw new IllegalStateException(String.format("Duplicate key %s", u));}, LinkedHashMap::new));

        model.addAttribute("nombre", competicion.getNombre());
        model.addAttribute("podios", podios);

        return "podios";
    }

    private List<ClasificadoRepository.Medalla> ordenarMedallas(List<ClasificadoRepository.Medalla> l) {

        List<ClasificadoRepository.Medalla> medallasOrdenadas = new ArrayList<>();
        medallasOrdenadas.add(l.stream().filter(m -> m.getRonda().equals(Clasificado.NombreRonda.MEDALLA_ORO.name())).findFirst().orElse(ClasificadoRepository.Medalla.builder().participante(Participante.builder().nombre("-").build()).build()));
        medallasOrdenadas.add(l.stream().filter(m -> m.getRonda().equals(Clasificado.NombreRonda.MEDALLA_PLATA.name())).findFirst().orElse(ClasificadoRepository.Medalla.builder().participante(Participante.builder().nombre("-").build()).build()));
        medallasOrdenadas.add(l.stream().filter(m -> m.getRonda().equals(Clasificado.NombreRonda.MEDALLA_BRONCE.name())).findFirst().orElse(ClasificadoRepository.Medalla.builder().participante(Participante.builder().nombre("-").build()).build()));

        return medallasOrdenadas;
    }

}
