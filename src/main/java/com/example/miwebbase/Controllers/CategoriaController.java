package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.PuntuacionTotal;
import com.example.miwebbase.Models.Resultado;
import com.example.miwebbase.Utils.AESUtils;
import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.TiempoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

    @Autowired CategoriaController self;


    @RequestMapping("/categoria/{nombreCategoria}/jornada/{numeroJornada}")
    public String getVistaCategoriaJornada(Model model, @PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("numeroJornada") int numeroJornada){

        Categoria categoria = self.getCategoria(nombreCategoria);
        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("resultado", self.getRankingJornada(categoria, numeroJornada));
        model.addAttribute("numJornadas", AESUtils.JORNADAS_CAMPEONATO);

        return "jornada";
    }


    @RequestMapping("/categoria/{nombreCategoria}")
    public String getVistaCategoria(Model model, @PathVariable("nombreCategoria") String nombreCategoria){

        Categoria categoria = self.getCategoria(nombreCategoria);
        model.addAttribute("categoria", categoria);
        model.addAttribute("posiciones", self.getRankingCategoria(categoria));
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("columnasJornadas", new int[AESUtils.JORNADAS_CAMPEONATO]);

        return "categoria";
    }


    @Cacheable(key = "#categoria.nombre", value = "puntuacionesGenerales")
    public List<PuntuacionTotal> getRankingCategoria(Categoria categoria){

        List<PuntuacionTotal> puntuacionesTotales = tiempoRepository.getParticipantesPuntosTotalesCategoria(categoria);

        List<PuntuacionTotal> puntuacionesTotalesAux = new ArrayList<>(puntuacionesTotales);

        // Ordenamos por puntuación y si no, sacamos la posición desempatada
        Comparator<PuntuacionTotal> comparadorPuntosYPosiciones =  ((Comparator<PuntuacionTotal>)(p1, p2) -> p2.getPuntuacion_total().compareTo(p1.getPuntuacion_total()))
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

    @Cacheable(key = "#categoria.nombre + #numeroJornada", value = "puntuacionesJornada")
    public List<Resultado.Categoria.Jornada> getRankingJornada(Categoria categoria, int numeroJornada){

        List<Tiempo> tiemposJornada = tiempoRepository.findAllByCategoriaAndJornadaOrderByPosicion(categoria, numeroJornada);

        Resultado.Categoria categoriaModel = new Resultado.Categoria(categoria);
        tiemposJornada.forEach(p -> categoriaModel.generarYAnadirJornada(p, categoria));

        return categoriaModel.getJornadas();

    }

    @Cacheable(value = "categorias")
    public Categoria getCategoria(String nombreCategoria){
        return  categoriaRepository.findByNombre(nombreCategoria);
    }

    @Cacheable(value = "listaDeCategorias")
    public List<Categoria> getCategoriasEnOrden(){
        return categoriaRepository.findAllByOrderByOrden();
    }


}
