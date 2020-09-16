package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Models.PuntuacionTotal;
import com.example.miwebbase.Utils.AESUtils;
import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.TiempoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.example.miwebbase.Utils.AESUtils.getPosicionDeParticipante;

@Controller

public class CategoriaController {

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    CategoriaRepository categoriaRepository;



    @RequestMapping("/categoria/{nombreCategoria}")
    public String inicio(Model model, @PathVariable("nombreCategoria") String nombreCategoria){

        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria);
        model.addAttribute("categoria", categoria);
        model.addAttribute("posiciones", getRankingCategoria(categoria));
        model.addAttribute("columnasJornadas", new int[AESUtils.JORNADAS_CAMPEONATO]);

        return "categoria";
    }


    public List<PuntuacionTotal> getRankingCategoria(Categoria categoria){

        List<PuntuacionTotal> puntuacionesTotales = tiempoRepository.getParticipantesPuntosTotalesCategoria(categoria);

        List<PuntuacionTotal> puntuacionesTotalesAux = new ArrayList<>(puntuacionesTotales);

        // Ordenamos por puntuación y si no, sacamos la posición desempatada
        Comparator<PuntuacionTotal> comparaPuntosDescendentemente = (p1, p2) -> p2.getPuntuacion_total().compareTo(p1.getPuntuacion_total());
        Comparator<PuntuacionTotal> comparadorPuntosYPosiciones = comparaPuntosDescendentemente
                .thenComparing(p -> {
                    p.setPosicion(getPosicionDeParticipante(p.getNombre(), categoria, puntuacionesTotalesAux, tiempoRepository));
                    return p.getPosicion();
                });

        puntuacionesTotales.sort(comparadorPuntosYPosiciones);

        AtomicInteger posicion = new AtomicInteger(1);
        puntuacionesTotales.forEach(p -> p.setPosicion(posicion.getAndIncrement()));


        puntuacionesTotales.forEach(pt -> pt.setPuntuacionesIndividuales(tiempoRepository.getParticipantesPuntosIndividualesCategoria(categoria).stream()
                .filter(pi -> pi.getNombre().equals(pt.getNombre())).collect(Collectors.toList())));

        return puntuacionesTotales;

    }

}
