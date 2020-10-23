package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Competicion;
import com.example.miwebbase.Models.Posicion;
import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.CompeticionRepository;
import com.example.miwebbase.repositories.DescalificacionRepository;
import com.example.miwebbase.repositories.TiempoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class RankingGeneralController {

    @Autowired
    TiempoRepository tiempoRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    CompeticionRepository competicionRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    @Autowired
    RankingGeneralController self;

    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}")
    public String getVistaCategoria(Model model, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("nombreCategoria") String nombreCategoria){

        Categoria categoria = self.getCategoria(nombreCategoria);
        Competicion competicion = self.getCompeticion(nombreCompeticion);

        model.addAttribute("competicion", competicion);
        model.addAttribute("categoria", categoria);
        model.addAttribute("posiciones", categoria.getRanking(competicion, descalificacionRepository));
        model.addAttribute("largoClasificacion", getLargoClasificacion(categoria.getRanking(competicion, descalificacionRepository), categoria.getCortePlayOffs()));
        model.addAttribute("categorias", self.getCategoriasEnOrden());

        return "categoria";
    }



    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}/jornada/{numeroJornada}")
    public String getVistaCategoriaJornada(Model model, @PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("numeroJornada") int numeroJornada){

        Categoria categoria = self.getCategoria(nombreCategoria);
        Competicion competicion = self.getCompeticion(nombreCompeticion);

        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("tiempos", categoria.getRankingJornada(competicion, numeroJornada));
        model.addAttribute("numJornadas", competicion.getNumJornadas());

        return "jornada";
    }


    @Cacheable(value = "categorias")
    public Categoria getCategoria(String nombreCategoria){
        return  categoriaRepository.findByNombre(nombreCategoria);
    }

    @Cacheable(value = "competiciones")
    public Competicion getCompeticion(String nombreCompeticion){
        return  competicionRepository.findByNombre(nombreCompeticion);
    }

    @Cacheable(value = "listaDeCategorias")
    public List<Categoria> getCategoriasEnOrden(){
        return categoriaRepository.findAllByOrderByOrden(); //TODO: solo las de la competicion en concreto
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
