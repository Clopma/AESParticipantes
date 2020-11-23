package com.example.aesparticipantes.Controllers;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Competicion;
import com.example.aesparticipantes.Entities.Evento;
import com.example.aesparticipantes.Entities.Tiempo;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.Repositories.*;
import com.example.aesparticipantes.Utils.AESUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RankingGeneralController {

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    EventoRepository eventoRepository;

    @Autowired
    CategoriaRepository categoriaRepository;
    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    @Autowired
    RankingGeneralController self;

    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}")
    public String getVistaCategoria(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("nombreCategoria") String nombreCategoria){

        Evento evento = self.getEvento(nombreCategoria, nombreCompeticion);

        List<Posicion> posiciones = self.getRankingGlobal(evento, descalificacionRepository, clasificadoRepository);

        model.addAttribute("evento", evento);
        model.addAttribute("posiciones", posiciones);
        model.addAttribute("largoClasificacion", getLargoClasificacion(posiciones, evento.getCortePlayOffs()));
        model.addAttribute("categorias", self.getCategoriasEnOrden());

        return "categoria";
    }



    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}/jornada/{numeroJornada}")
    public String getVistaCategoriaJornada(Model model, @PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("numeroJornada") int numeroJornada){

        Evento evento = self.getEvento(nombreCategoria, nombreCompeticion);

        model.addAttribute("evento", evento);
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("tiempos", self.getRankingJornada(evento, numeroJornada));
        model.addAttribute("numJornadas", evento.getCompeticion().getJornadas().size());

        return "jornada";
    }


    @Cacheable(value = "eventos")
    public Evento getEvento(String nombreCategoria, String nombreCompeticion){
        return  eventoRepository.findByCategoriaNombreAndCompeticionNombre(nombreCategoria, nombreCompeticion);
    }

    @Cacheable(value = "competiciones")
    public Competicion getCompeticion(String nombreCompeticion){
        return  competicionRepository.findByNombre(nombreCompeticion);
    }

    @Cacheable(value = "listaDeCategorias")
    public List<Categoria> getCategoriasEnOrden(){
        return categoriaRepository.findAllByOrderByOrden(); //TODO: solo las de la competicion en concreto
    }

    @Cacheable(value = "rankingsGlobales", key = "#evento.id")
    public List<Posicion> getRankingGlobal(Evento evento, DescalificacionRepository descalificacionRepository, ClasificadoRepository clasificadoRepository) {

        evento.getTiempos().stream().collect(Collectors.groupingBy(Tiempo::getJornada)).forEach((k, v) -> AESUtils.setPosicionesEnTiempos(v));
        return evento.getRankingGlobal(descalificacionRepository, clasificadoRepository);
    }


    @Cacheable(value = "rankingsJornada", key = "#evento.id+ '-' + #numeroJornada")
    public List<Tiempo> getRankingJornada(Evento evento, int numeroJornada){
        return evento.getRankingJornada(numeroJornada);
    }

    private int getLargoClasificacion(List<Posicion> puntuaciones, int largoPredeterminado) {

        int largoClasificacion = 0;
        int numClasificados = 0;
        for (Posicion puntuacion : puntuaciones){

            if(numClasificados == largoPredeterminado){
                break;
            }

            if(puntuacion.isClasificado()){
                numClasificados++;
            }

            largoClasificacion++;
        }

        return largoClasificacion;
    }



}
