package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Competicion;
import com.example.miwebbase.Entities.Descalificacion;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.PuntuacionIndividual;
import com.example.miwebbase.Models.RankingCategoriaParticipante;
import com.example.miwebbase.Models.ResultadoCompeticion;
import com.example.miwebbase.Utils.AESUtils;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

        List<RankingCategoriaParticipante> puntuaciones = self.getRankingsCategoria(categoria, competicion);

        model.addAttribute("competicion", competicion);
        model.addAttribute("categoria", categoria);
        model.addAttribute("posiciones", puntuaciones);
        model.addAttribute("largoClasificacion", getLargoClasificacion(puntuaciones, categoria.getCortePlayOffs()));
        model.addAttribute("categorias", self.getCategoriasEnOrden());

        return "categoria";
    }

    private int getLargoClasificacion(List<RankingCategoriaParticipante> puntuaciones, int largoPredeterminado) {

        int largoClasificacion = 0;
        int numClasificados = 0;
        for (RankingCategoriaParticipante puntuacion : puntuaciones){

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

    @RequestMapping("/ranking/{nombreCompeticion}/{nombreCategoria}/jornada/{numeroJornada}")
    public String getVistaCategoriaJornada(Model model, @PathVariable("nombreCategoria") String nombreCategoria, @PathVariable("nombreCompeticion") String nombreCompeticion, @PathVariable("numeroJornada") int numeroJornada){

        Categoria categoria = self.getCategoria(nombreCategoria);
        Competicion competicion = self.getCompeticion(nombreCompeticion);

        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", self.getCategoriasEnOrden());
        model.addAttribute("resultado", self.getRankingJornada(categoria, numeroJornada));
        model.addAttribute("numJornadas", competicion.getNumJornadas());

        return "jornada";
    }


    @Cacheable(key = "#categoria.nombre + '-' + #competicion.nombre", value = "puntuacionesGenerales")
    public List<RankingCategoriaParticipante> getRankingsCategoria(Categoria categoria, Competicion competicion){

        List<RankingCategoriaParticipante> puntuacionesTotales = tiempoRepository.getParticipantesPuntosTotalesCategoria(categoria, competicion);

        List<RankingCategoriaParticipante> puntuacionesTotalesAux = new ArrayList<>(puntuacionesTotales);

        // Ordena por puntuación y si no, por puntuación desempatada
        Comparator<RankingCategoriaParticipante> comparadorPuntosYPosiciones =  ((Comparator<RankingCategoriaParticipante>)(p1, p2) -> p2.getPuntuacion_total().compareTo(p1.getPuntuacion_total()))
                .thenComparing(p -> getPosicionDeParticipante(p.getNombreParticipante(), categoria, puntuacionesTotalesAux, tiempoRepository, competicion));

        puntuacionesTotales.sort(comparadorPuntosYPosiciones);

        // Setea todas las posiciones tal y como se han ordenado
        AtomicInteger posicion = new AtomicInteger(1);
        puntuacionesTotales.forEach(p -> p.setPosicion(posicion.getAndIncrement()));

        puntuacionesTotales.forEach(pt -> pt.setPuntuacionesIndividuales(
                tiempoRepository.getParticipantesPuntosIndividualesCategoria(categoria, competicion).stream()
                .filter(pi -> pi.getNombre().equals(pt.getNombreParticipante())).collect(Collectors.toList()), competicion));


        List<Descalificacion> descalificados = descalificacionRepository.findAllByCategoria(categoria);


        List<RankingCategoriaParticipante> noDescalificados = puntuacionesTotales.stream().filter(
                p -> descalificados.stream().noneMatch(d -> p.getNombreParticipante().equals(d.getParticipante().getNombre())))
                .collect(Collectors.toList());

                noDescalificados.subList(0, Math.min(categoria.getCortePlayOffs(), noDescalificados.size() - 1)) //TODO: TEST
                .forEach(p -> p.setClasificado(true));

        return puntuacionesTotales;

    }

    @Cacheable(key = "#categoria.nombre + #numeroJornada", value = "puntuacionesJornada")
    public List<ResultadoCompeticion.Categoria.Jornada> getRankingJornada(Categoria categoria, int numeroJornada){

        List<Tiempo> tiemposJornada = tiempoRepository.findAllByCategoriaAndJornadaOrderByPosicion(categoria, numeroJornada);

        ResultadoCompeticion.Categoria categoriaModel = new ResultadoCompeticion.Categoria(categoria);
        tiemposJornada.forEach(p -> categoriaModel.generarYAnadirJornada(p, categoria));

        return categoriaModel.getJornadas();

    }

    public RankingCategoriaParticipante getRankingParticipante(Categoria categoria, Competicion competicion, String nombreParticipante) {
        return getRankingsCategoria(categoria, competicion).stream().filter(r -> nombreParticipante.equals(r.getNombreParticipante())).findFirst().orElse(null);
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


    private static Integer getPosicionDeParticipante(String nombreParticipante, Categoria categoria, List<RankingCategoriaParticipante> puntuaciones, TiempoRepository tiempoRepository, Competicion competicion) {

        RankingCategoriaParticipante puntuacionTotalParticipantes = puntuaciones.stream()
                .filter(p -> p.getNombreParticipante().equals(nombreParticipante))
                .findFirst()
                .get().clone();

        List<RankingCategoriaParticipante> puntuacionesEmpatadasOriginalmentePorPuntuacionTotal = puntuaciones.stream().map(RankingCategoriaParticipante::clone)
                .filter(p -> p.getPuntuacion_total().intValue() == puntuacionTotalParticipantes.getPuntuacion_total().intValue()).collect(Collectors.toList());

        if (puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.size() == 1) {
            return puntuaciones.indexOf(puntuaciones.stream().filter(p -> p.getNombreParticipante().equals(nombreParticipante)).findFirst().get()) + 1;
        }

        puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.forEach(p -> p.setPuntuacionesIndividuales(tiempoRepository.getParticipantePuntosIndividualesCategoria(categoria, p.getNombreParticipante(), competicion), competicion)); //TODO maybe get todo y luego filter

        List<RankingCategoriaParticipante> puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(RankingCategoriaParticipante::clone).collect(Collectors.toList());

        List<String> personasDesempatadasEnOrden = new ArrayList<>();

        //Mientras el nombre que buscamos no esté ordenado
        while (!personasDesempatadasEnOrden.stream().anyMatch(p -> p.equals(nombreParticipante))) {

            List<RankingCategoriaParticipante> puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas =
                    puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream().map(RankingCategoriaParticipante::clone).collect(Collectors.toList());

            puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> p.getPuntuacionesIndividuales().sort(Comparator.comparingInt(PuntuacionIndividual::getPuntuacion_jornada).reversed()));

            List<RankingCategoriaParticipante> puntuacionesEmpatadasPorPuntuacionesIndividuales;
            while (true) {

                int mejorPuntuacionJornadaN = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream()
                        .mapToInt(ps -> ps.getPuntuacionesIndividuales().size() > 0 ? ps.getPuntuacionesIndividuales().get(0).getPuntuacion_jornada() : 0)
                        .max().getAsInt();

                List<RankingCategoriaParticipante> aux = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.stream().map(RankingCategoriaParticipante::clone)
                        .filter(p -> p.getPuntuacionesIndividuales().size() > 0 && p.getPuntuacionesIndividuales().get(0).getPuntuacion_jornada() == mejorPuntuacionJornadaN)
                        .collect(Collectors.toList());

                if (aux.size() > 1) {

                    puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas.forEach(p -> p.getPuntuacionesIndividuales().remove(0));

                } else if (aux.size() == 1) {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = aux;
                    break;

                } else /*if (aux.size() == 0)*/ {

                    puntuacionesEmpatadasPorPuntuacionesIndividuales = puntuacionesEmpatadasActualmentePorPuntuacionTotalConJornadasOrdenadas;
                    break;

                }

            }

            if (puntuacionesEmpatadasPorPuntuacionesIndividuales.size() == 1) {

                String personaConMejorPuntuacionIndividual = puntuacionesEmpatadasPorPuntuacionesIndividuales.get(0).getNombreParticipante();
                personasDesempatadasEnOrden.add(personaConMejorPuntuacionIndividual);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getNombreParticipante().equals(personaConMejorPuntuacionIndividual))
                        .collect(Collectors.toList());

            } else {

                //Desempatar por media
                List<Tiempo> tiemposEmpatados = tiempoRepository.getTiemposDeVariosParticipantes(categoria, puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().map(RankingCategoriaParticipante::getNombreParticipante).collect(Collectors.toSet()), competicion);
                tiemposEmpatados.forEach(t -> t.setMedia(AESUtils.getTiemposCalculados(Arrays.asList(t.getTiempo1(), t.getTiempo2(), t.getTiempo3(), t.getTiempo4(), t.getTiempo5()), categoria)[1]));

                Tiempo mejorMedia = tiemposEmpatados.stream().reduce((acc, val) -> (acc.getMedia() > 0) && (acc.getMedia() < val.getMedia()) ? acc : val).get();

                String personaConMejorMedia = puntuacionesEmpatadasPorPuntuacionesIndividuales.stream().filter(p -> p.getNombreParticipante().equals(mejorMedia.getParticipante().getNombre())).findFirst().get().getNombreParticipante();
                personasDesempatadasEnOrden.add(personaConMejorMedia);
                puntuacionesEmpatadasActualmentePorPuntuacionTotal = puntuacionesEmpatadasActualmentePorPuntuacionTotal.stream()
                        .filter(p -> !p.getNombreParticipante().equals(personaConMejorMedia))
                        .collect(Collectors.toList());

            }
        }

        List<String> nombresEmpatados = puntuacionesEmpatadasOriginalmentePorPuntuacionTotal.stream().map(RankingCategoriaParticipante::getNombreParticipante).collect(Collectors.toList());
        int posicionParticipanteSuperior = puntuaciones.indexOf(puntuaciones.stream()
                .filter(p -> nombresEmpatados.contains(p.getNombreParticipante()))
                .findFirst()
                .get());

        int posicionParticipante = personasDesempatadasEnOrden.indexOf(personasDesempatadasEnOrden.stream()
                .filter(nombreParticipante::equals)
                .findFirst()
                .get()) + 1;

        return posicionParticipanteSuperior + posicionParticipante;
    }


}
