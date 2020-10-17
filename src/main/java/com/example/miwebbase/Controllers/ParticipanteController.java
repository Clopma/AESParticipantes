package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.Resultado;
import com.example.miwebbase.repositories.TiempoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ParticipanteController {

    @Autowired
    private TiempoRepository tiempoRepository;

    @Autowired
    private CategoriaController categoriaController;

    @Autowired ParticipanteController self;

    @RequestMapping("/participante/{nombreParticipante}")
    public String inicio(Model model, @PathVariable("nombreParticipante") String nombreParticipante) throws Exception {

        model.addAttribute("participante", nombreParticipante);

        model.addAttribute("resultado", self.getResultadoParticipante(nombreParticipante));

        return "participante";
    }

    @Cacheable(value = "participantes")
    public Resultado getResultadoParticipante(String nombreParticipante) {

        List<Tiempo> tiemposParticipante = tiempoRepository.getTiemposOfParticipante(nombreParticipante);

        Map<Integer, List<Tiempo>> categoriasInformadas = tiemposParticipante.stream().collect(Collectors.groupingBy(t -> t.getCategoria().getOrden()));

        Resultado resultado = new Resultado();
        resultado.setCategoriasParticipadas(new ArrayList<>());

        for (Map.Entry<Integer, List<Tiempo>> categoriaEntry : categoriasInformadas.entrySet()) {
            resultado.generarYAnadirCategoria(categoriaEntry.getValue(), categoriaController, nombreParticipante);
        }

        return resultado;
    }


}
