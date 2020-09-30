package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Clasificado;
import com.example.miwebbase.Entities.Descalificacion;
import com.example.miwebbase.Entities.Participante;
import com.example.miwebbase.Models.PuntuacionTotal;
import com.example.miwebbase.repositories.CategoriaRepository;
import com.example.miwebbase.repositories.ClasificadoRepository;
import com.example.miwebbase.repositories.DescalificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CalendarioController {

    @Autowired
    ClasificadoRepository clasificadoRepository;

    @Autowired
    CategoriaController categoriaController;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    DescalificacionRepository descalificacionRepository;

    List<Integer> ordenCuartos = Arrays.asList(0, 7, 4, 3, 2, 5, 6, 1);
    List<Integer> ordenSemifinales = Arrays.asList(0, 3, 2, 1);

    @GetMapping("/calendario")
    public String showForm(Model model) {
        return "calendario";
    }


    @GetMapping("/calendario/playoffs/{categoria}")
    public String evento(@PathVariable("categoria") String nombreCategoria, Model model) {

        Categoria categoria = categoriaRepository.findByNombre(nombreCategoria);

        model.addAttribute("categoria", categoria);

        if(categoria.getCortePlayOffs() == 8){
            model.addAttribute("listaCuartos", iniciarBracket(categoriaController.getRankingCategoria(categoria), descalificacionRepository.findAllByCategoria(categoria), 8));
            model.addAttribute("listaSemis", rellenarBracket(clasificadoRepository.getRonda(categoria, Clasificado.NombreRonda.SEMIFINAL.name()), 4, categoria));
        } else if (categoria.getCortePlayOffs() == 4) {
            model.addAttribute("listaSemis", iniciarBracket(categoriaController.getRankingCategoria(categoria), descalificacionRepository.findAllByCategoria(categoria), 4));
        }

        model.addAttribute("listaFinal", rellenarBracket(clasificadoRepository.getRonda(categoria, Clasificado.NombreRonda.FINAL.name()), 2, categoria));

        return "fragments/bracket";
    }


    @GetMapping("/calendario/evento/{evento}")
    public String evento(@PathVariable("evento") String evento) {
        return "fragments/eventos/"+evento;
    }

    private Clasificado[] rellenarBracket(List<Clasificado> clasificados, int numClasificados, Categoria categoria) {
        Clasificado[] rondas = new Clasificado[numClasificados];
        for (int i = 0; i < numClasificados; i++) {
            int finalI = i;
            rondas[i] = clasificados.stream().filter(c -> c.getPosicion() == finalI).findFirst().orElse(
                    Clasificado.builder().participante(Participante.builder().nombre(categoria.getCortePlayOffs() == numClasificados ? puestoEnLugar(finalI, numClasificados) : "-").build()).posicion(i).build());
        }
        return rondas;
    }

    private String puestoEnLugar(int finalI, int numClasificados) {

        if (numClasificados == 8) {
            return "Posición " + ordenCuartos.get(finalI);
        }

        if (numClasificados == 4) {
            return "Posición " + ordenSemifinales.get(finalI);
        }

        return "Posición " + (finalI + 1);


    }


    private Clasificado[] iniciarBracket(List<PuntuacionTotal> rankingCategoria, List<Descalificacion> descalificados, int numClasificados) {

        List<Clasificado>  clasificados = rankingCategoria.stream().filter(p -> descalificados.stream()
                .noneMatch(d -> p.getNombre().equals(d.getParticipante().getNombre())))
                .map(p -> Clasificado.builder().participante(Participante.builder().nombre(p.getNombre()).build()).build())
                .collect(Collectors.toList())
                .subList(0, numClasificados);

        Clasificado[] clasificadosEnOrden = new Clasificado[numClasificados];

       for(int i = 0; i < numClasificados; i++){
           clasificadosEnOrden[numClasificados == 8 ? ordenCuartos.get(i) : ordenSemifinales.get(i)] = clasificados.get(i);
       }

       return clasificadosEnOrden;
    }

}
