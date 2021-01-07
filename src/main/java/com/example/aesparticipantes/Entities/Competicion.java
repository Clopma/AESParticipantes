package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Table(name = "Competiciones")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Competicion {

    @Id
    @Column(length = 25)
    private String nombre;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "competicion")
    @Fetch(FetchMode.SELECT)
    private List<Evento> eventos;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "competicion") //TODO 0003 falla en participante, a veces, difícil de reprocucir. Probar con reset -> participante -> participar (debes estar logueado y haber competicion en curso) -> inicio -> loguear
    @Fetch(FetchMode.SELECT)
    private List<Jornada> jornadas;

    @Column(length = 1000)
    private String descripcion;

    public Date getInicio(){
        Optional<Jornada> primeraJornda = jornadas.stream().filter(j -> j.getNumeroJornada()==1).findFirst();
        return primeraJornda.map(Jornada::getFechaInicio).orElse(null);
    }

    public String getInicioStr(){
        return AESUtils.dateToString(getInicio());
    }

    public Date getFinalizacion(){
        Optional<Jornada> ultimaJornada = jornadas.stream().reduce((acc, val) -> acc.getFechaFin().after(val.getFechaFin()) ? acc : val); //Get jornada con fechaFin más lejana
        return ultimaJornada.map(Jornada::getFechaFin).orElse(null);
    }

    public boolean isFinalizada(){
        return getFinalizacion().before(new Date());
    }

    public boolean isEnCurso(){
        return  !isFinalizada()
                &&
                getInicio().before(new Date()) /* is empezada */;
    }
    
    //TODO si quiero sacar los participantes inscritos puedo sacarlos desde eventos y si se actualiza, estará la lista actualizada? (hibernate lo handlea), o tengo que hacer una query en el controller con cacheable (yo lo handleo)?

    //TODO: quizá mejor query? (Y añadir quizá el count de inscripciones/evento)
    public TreeMap<Participante, Map<String, Categoria>> getCategoriasInscritasPorParticipanteMap(){
        List<Inscripcion> inscripciones = new ArrayList<>();
        eventos.forEach(e -> inscripciones.addAll(e.getInscripciones()));
        return new TreeMap<>(inscripciones.stream().collect(Collectors.groupingBy(Inscripcion::getParticipante)).entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey, m -> m.getValue().stream().collect(Collectors.toMap(i -> i.getEvento().getCategoria().getNombre(), i -> i.getEvento().getCategoria())))));
    }


    public boolean isParticipanteInscrito(Participante p){
        if (p == null) return false;
        return eventos.stream().anyMatch(i -> i.isParticipanteInscrito(p)); //TODO: comprobar si al llamar dos veces desde el front hace dos llamadas o se cachea, por curiosidad
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Competicion)) {
            return false;
        }

        Competicion c = (Competicion) o;

        return c.getNombre().equals(this.getNombre());
    }

    public boolean isEmpezada() {
        return this.getInicio().before(new Date());
    }

    public Optional<Jornada> getJornadaActiva() {
        return getJornadas().stream().filter(j -> j.getFechaInicio().before(new Date()) && j.getFechaFin().after(new Date())).findFirst();
    }
}
