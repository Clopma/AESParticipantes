package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.KeyJornada;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;


@Table(name = "Jornadas")
@IdClass(KeyJornada.class)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Jornada implements Comparable{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Competicion competicion;

    @Id
    private int numeroJornada;

    @LazyCollection(LazyCollectionOption.FALSE) //TODO quitar?
    @OneToMany(mappedBy = "jornada")
    private Set<Tiempo> tiempos;

    @NotNull
    private Date fechaInicio;

    @NotNull
    private Date fechaFin;

    public String getFechaInicioStr(){
        return AESUtils.dateToString(fechaInicio);
    }

    public String getFechaFinStr(){
        return AESUtils.dateToString(fechaFin);
    }

    public boolean isAcabada(){
        return fechaFin.before(new Date());
    }


    @Override
    public int compareTo(Object o) {
        return getNumeroJornada() - ((Jornada) o).getNumeroJornada();
    }
}
