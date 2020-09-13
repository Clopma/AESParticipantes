package com.example.miwebbase.Controllers;

import com.example.miwebbase.Entities.Categoria;
import com.example.miwebbase.Entities.Tiempo;
import com.example.miwebbase.Models.Resultado;
import com.example.miwebbase.Utils.RubikUtils;
import com.example.miwebbase.repositories.TiempoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TiemposController {

    @Autowired
    private TiempoRepository tiempoRepository;


    @RequestMapping("/participante/{nombre}")
    public String inicio(Model model, @PathVariable("nombre") String nombreParticipante) throws Exception {

        model.addAttribute("participante", nombreParticipante);
        List<Tiempo> tiemposParticipante = tiempoRepository.getTiemposOfParticipante(nombreParticipante);

        Map<Integer, List<Tiempo>> categoriasInformadas = tiemposParticipante.stream().collect(Collectors.groupingBy(t -> t.getCategoria().getOrden()));

        Resultado resultado = new Resultado();
        resultado.setCategoriasParticipadas(new ArrayList<>());
        for (Map.Entry<Integer, List<Tiempo>> categoriaEntry : categoriasInformadas.entrySet()) {

            Resultado.Categoria categoriaObj = new Resultado.Categoria();
            categoriaObj.setJornadasParticipadas(new ArrayList<>());
            Categoria categoria = categoriaEntry.getValue().get(0).getCategoria();
            categoriaObj.setNombre(categoria.getNombre());
            categoriaObj.setNumTiempos(categoria.getNumTiempos());
            categoriaObj.setPosicion(categoria.getPosicionDeParticipante(nombreParticipante, categoria, tiempoRepository));
            //categoriaObj.setTamano(3600/(Math.pow(categoriaObj.getPosicion(), 2) + 118)+10);

            resultado.getCategoriasParticipadas().add(categoriaObj);

            Map<Integer, List<Tiempo>> jornadasInformadas = categoriaEntry.getValue().stream().collect(Collectors.groupingBy(c -> c.getJornada()));

            for (Map.Entry<Integer, List<Tiempo>> jornada : jornadasInformadas.entrySet()) {


                Resultado.Categoria.Jornada jornadaObj = new Resultado.Categoria.Jornada();
                jornadaObj.setNumJornada(jornada.getKey());
                if (jornada.getValue().size() > 1) {
                    throw new Exception("Varios valores para la misma jornada");
                }
                Tiempo tiempo = jornada.getValue().get(0);
                jornadaObj.setPosicion(tiempo.getPosicion());
                jornadaObj.setSolucion(tiempo.getSolucion() == null ? "" : tiempo.getSolucion());
                jornadaObj.setExplicacion(tiempo.getExplicacion() == null ? "" : tiempo.getExplicacion());

                List<Double> tiempos = new ArrayList<>();
                tiempos.add(tiempo.getTiempo1());
                tiempos.add(tiempo.getTiempo2());
                tiempos.add(tiempo.getTiempo3());
                tiempos.add(tiempo.getTiempo4());
                tiempos.add(tiempo.getTiempo5());

                jornadaObj.setTiempo1(tiempo.getTiempo1() == 0 ? RubikUtils.DNF : formatTime(tiempo.getTiempo1()));

                if (categoria.getNumTiempos() > 1) {

                    jornadaObj.setTiempo2(tiempo.getTiempo2() == 0 ? RubikUtils.DNF : formatTime(tiempo.getTiempo2()));
                    jornadaObj.setTiempo3(tiempo.getTiempo3() == 0 ? RubikUtils.DNF : formatTime(tiempo.getTiempo3()));
                }

                if (categoria.getNumTiempos() > 3) {

                    jornadaObj.setTiempo4(tiempo.getTiempo4() == 0 ? RubikUtils.DNF : formatTime(tiempo.getTiempo4()));
                    jornadaObj.setTiempo5(tiempo.getTiempo5() == 0 ? RubikUtils.DNF : formatTime(tiempo.getTiempo5()));
                }


                double[] singleYMedia = RubikUtils.getTiemposCalculados(tiempos, categoria);
                jornadaObj.setSingle(singleYMedia[0] == 0 ? RubikUtils.DNF : formatTime(singleYMedia[0]));
                jornadaObj.setMedia(singleYMedia[1] == 0 ? RubikUtils.DNF : formatTime(singleYMedia[1]));

                jornadaObj.setPuntos(tiempo.getPuntosTotales());
                categoriaObj.getJornadasParticipadas().add(jornadaObj);
            }
        }


        model.addAttribute("resultado", resultado);

        return "tiempo";
    }


    private String formatTime(double time) {

        try {
            String doubleAsText = String.valueOf(round(time, 2));
            int decimal = Integer.parseInt(doubleAsText.split("\\.")[0]);
            int mins = decimal / 60;
            int secs = decimal % 60;
            String fractional = doubleAsText.split("\\.")[1];
            if (fractional.length() == 1) {
                fractional += "0";
            }
            if (mins > 0) {
                return String.format("%01d:%02d.", mins, secs) + fractional;
            } else {
                return String.format("%01d.", secs) + fractional;
            }
        } catch (Exception e) {
            return "CCA";
        }
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
