package com.example.aesparticipantes.Entities.Comparators;

import com.example.aesparticipantes.Entities.Evento;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class EventoComparator implements Comparator<Evento> {

    @Override
    public int compare(Evento o1, Evento o2) {
        return o1.getCategoria().getOrden() - o2.getCategoria().getOrden();
    }
}
