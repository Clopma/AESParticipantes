package com.example.aesparticipantes.Utils;

import com.example.aesparticipantes.Entities.Categoria;
import com.example.aesparticipantes.Entities.Clasificado;
import com.example.aesparticipantes.Entities.Tiempo;
import com.example.aesparticipantes.Models.Posicion;
import com.example.aesparticipantes.Repositories.ClasificadoRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AESUtils {

    public static final String MENSAJE_ERROR = "Ha habido un error con la autenticación WCA, por favor, contacta un administrador y enséñale este mensaje: ";

    public static Collator COLLATOR = Collator.getInstance();

    public enum TiposUsuarios {
        NV, // NO VINCULADO
        NC, // NO CONFIRMADO
        C, // VINCULADO
        N // NINGUNO
    }

    public static String DNF = "DNF";

    //devuelve un double[]{single, media, peor}
    public static double[] getTiemposCalculados(List<Double> tiempos, int numTiempos) {

        tiempos = tiempos.stream().filter(t -> t > 0).collect(Collectors.toList());

        double mejorTiempo = tiempos.stream().mapToDouble(d -> d).min().orElse(0);

        Collections.sort(tiempos);
        double peorTiempo = 0;
        double media = 0;
        if (numTiempos == 5) {

            if (tiempos.size() == 4) {
                tiempos.remove(0);
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
            } else if (tiempos.size() == 5) {
                tiempos.remove(0);
                peorTiempo = tiempos.get(4 - 1);
                tiempos.remove(4 - 1);
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
            }
        } else if (numTiempos == 3) {
            if (tiempos.size() == 3) {
                media = tiempos.stream().mapToDouble(d -> d).average().getAsDouble();
                peorTiempo = tiempos.get(2);
            }
        }

        return new double[]{mejorTiempo, media, peorTiempo};
    }

    public static String formatTime(double time, Categoria categoria) {

        try {
            if(categoria.getNombre().equals("FMC")){
                return (int) time + "";
            }
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getPosicionFinal(List<Clasificado.NombreRonda> clasificaciones) {

        if (clasificaciones.size() > 0) {
            List<Clasificado.NombreRonda> order = Arrays.asList(Clasificado.NombreRonda.MEDALLA_BRONCE, Clasificado.NombreRonda.MEDALLA_PLATA, Clasificado.NombreRonda.MEDALLA_ORO);

            Clasificado.NombreRonda max = clasificaciones.get(0);

            for (Clasificado.NombreRonda c : clasificaciones) {
                if (order.indexOf(max) < order.indexOf(c)) {
                    max = c;
                }
            }

            return max.name();

        }

        return null;
    }

    public static void setMedallas(List<Posicion> posiciones, ClasificadoRepository clasificadoRepository) {
        posiciones.stream().filter(Posicion::isClasificado).collect(Collectors.toList()).forEach(p -> p.setMedalla(clasificadoRepository));
    }


    public static String getHash(String str){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    str.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            return "error"; //Imposible
        }
    }

    //Asume que se ha llamado a calcular Datos en los tiempos de la lista
    public static void setPosicionesEnTiempos(List<Tiempo> tiemposJornada){

        tiemposJornada.forEach(Tiempo::calcularDatos);
        Collections.sort(tiemposJornada);

        if (tiemposJornada.size() == 0) {
            return;
        }

        double mejorMediaJornada = tiemposJornada.get(0).getMedia();
        double mejorSingleJornada = tiemposJornada.get(0).getSingle();

        String nombreCategoria = tiemposJornada.get(0).getCategoria().getNombre();

        for(int i = 0; i < tiemposJornada.size(); i++){
            tiemposJornada.get(i).setPosicion(i+1);

            if ("FMC".equals(nombreCategoria) || "BLD".equals(nombreCategoria)){
                tiemposJornada.get(i).setPuntosTiempo(tiemposJornada.get(i).getSingle() == 0 ? 0 : (int) Math.round(mejorSingleJornada*100/tiemposJornada.get(i).getSingle()));
                if (tiemposJornada.get(i).getSingle() != 0) {
                    if(i > 0 && tiemposJornada.get(i).getSingle() == tiemposJornada.get(i-1).getSingle()){
                        tiemposJornada.get(i).setPuntosBonus(tiemposJornada.get(i-1).getPuntosBonus());
                    } else {
                        tiemposJornada.get(i).setPuntosBonus(AESUtils.puntosEnPosicion(i + 1)); // else por defecto es 0
                    }
                }
            } else {
                tiemposJornada.get(i).setPuntosTiempo(tiemposJornada.get(i).getMedia() == 0 ? 0 : (int) Math.round(mejorMediaJornada*100/tiemposJornada.get(i).getMedia()));
                if (tiemposJornada.get(i).getMedia() != 0) {
                    if(i > 0 && tiemposJornada.get(i).getMedia() == tiemposJornada.get(i-1).getMedia()){
                        tiemposJornada.get(i).setPuntosBonus(tiemposJornada.get(i-1).getPuntosBonus());
                    } else {
                        tiemposJornada.get(i).setPuntosBonus(AESUtils.puntosEnPosicion(i + 1)); // else por defecto es 0
                    }
                }
            }

        }


    }

    private static int puntosEnPosicion(int i) {

        switch (i){
            case 1: return 25;
            case 2: return 19;
            case 3: return 15;
            case 4: return 12;
            case 5: return 10;
            case 6: return 8;
            case 7: return 6;
            case 8: return 5;
            case 9: return 4;
            case 10: return 3;
            case 11: return 2;
            case 12: return 1;
            default: return 0;
        }


    }


    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static String dateToString(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return   dateFormat.format(date);
    }





}
