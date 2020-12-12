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
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class RankingGeneralController {

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    CompeticionController competicionController;

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

        Optional<Competicion> competicion = competicionController.getCompeticion(nombreCompeticion);
        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria); //TODO Cachear

        if(!competicion.isPresent() || categoria == null){
            return "error/404";
        }

        Evento evento = self.getEvento(categoria, competicion.get());

        List<Posicion> posiciones = self.getRankingGlobal(evento, descalificacionRepository, clasificadoRepository);
        List<Evento> eventosCompeticionEnOrden = self.getEventosEnOrden(competicion.get());

        model.addAttribute("evento", evento);
        model.addAttribute("anteriorEvento", anteriorEvento(eventosCompeticionEnOrden, evento));
        model.addAttribute("siguienteEvento", siguenteEvento(eventosCompeticionEnOrden, evento));
        model.addAttribute("posiciones", posiciones);
        model.addAttribute("largoClasificacion", getLargoClasificacion(posiciones, evento.getCortePlayOffs()));
        model.addAttribute("eventos", self.getEventosEnOrden(competicion.get()));

        return "categoria";
    }



    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}/jornada/{numeroJornada}")
    public String getVistaCategoriaJornada(Model model, @PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("numeroJornada") int numeroJornada){

        Optional<Competicion> competicion = competicionRepository.findByNombre(nombreCompeticion);
        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria);

        if(!competicion.isPresent() || categoria == null){
            return "error/404";
        }

        Evento evento = self.getEvento(categoria, competicion.get());

        List<Evento> eventosCompeticionEnOrden = self.getEventosEnOrden(competicion.get());
        model.addAttribute("evento", evento);
        model.addAttribute("anteriorEvento", anteriorEvento(eventosCompeticionEnOrden, evento));
        model.addAttribute("siguienteEvento", siguenteEvento(eventosCompeticionEnOrden, evento));
        model.addAttribute("tiempos", self.getRankingJornada(evento, numeroJornada));
        model.addAttribute("numJornadas", evento.getCompeticion().getJornadas().size());

        return "jornada";
    }


    @Cacheable(value = "eventos", key = "#categoria.nombre+'-'+ #competicion.nombre")
    public Evento getEvento(Categoria categoria, Competicion competicion){
        return  eventoRepository.findByCategoriaAndCompeticion(categoria, competicion);
    }

    @Cacheable(value = "listaDeCategorias", key = "#competicion.nombre")
    public List<Evento> getEventosEnOrden(Competicion competicion){
        return eventoRepository.getEventosDeCompeticionPorOrdenDeCategoria(competicion);
    }

    @Cacheable(value = "rankingsGlobales", key = "#evento.id")
    public List<Posicion> getRankingGlobal(Evento evento, DescalificacionRepository descalificacionRepository, ClasificadoRepository clasificadoRepository) {

        evento.getTiempos().stream().collect(Collectors.groupingBy(Tiempo::getJornada)).forEach((j, ts) -> AESUtils.setPosicionesEnTiempos(ts));
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

    public Optional<Evento> anteriorEvento(List<Evento> eventos, Evento evento){

        int actualIndex = eventos.indexOf(evento);
        return actualIndex > 0 ? Optional.of(eventos.get(actualIndex - 1)) : Optional.empty();

    }

    private Optional<Evento> siguenteEvento(List<Evento> eventos, Evento evento) {

        int actualIndex = eventos.indexOf(evento);
        return actualIndex < eventos.size() - 1 ? Optional.of(eventos.get(actualIndex + 1)) : Optional.empty();

    }





}
