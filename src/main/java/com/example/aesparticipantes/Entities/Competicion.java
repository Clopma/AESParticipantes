package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Comparators.EventoComparator;
import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;
import org.hibernate.annotations.SortComparator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Table(name = "Competiciones")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Competicion implements Comparable {

    @Id
    @Column(length = 25)
    private String nombre;

    @ManyToOne()
    private Temporada temporada;

    @OneToMany(mappedBy = "competicion")
    @SortComparator(EventoComparator.class)
    private Set<Evento> eventos;

    @OneToMany(mappedBy = "competicion")
    @org.hibernate.annotations.OrderBy(clause = "numeroJornada asc")
    private Set<Jornada> jornadas;

    @OneToMany(mappedBy = "competicion")
    @org.hibernate.annotations.OrderBy(clause = "orden asc")
    private List<SorteoCompeticion> sorteosCompeticion;

    @Column(length = 1000)
    private String descripcion;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date limiteInscripciones;


    public Optional<Jornada> getJornada(int numeroJornada){
        return getJornadas().stream().filter(j -> j.getNumeroJornada() == numeroJornada).findAny();
    }

    public Jornada getUltimaJornada(){
        return getJornadas().stream().reduce((acc, val) -> acc.getNumeroJornada() > val.getNumeroJornada() ? acc : val).get();
    }

    public Jornada getPrimeraJornada(){
        return getJornadas().stream().filter(j -> j.getNumeroJornada() == 1).findAny().get();
    }

    public String getFechaLimiteInscripcionesStr(){
        return AESUtils.dateToString(limiteInscripciones);
    }

    public Date getInicio(){
        Optional<Jornada> primeraJornda = jornadas.stream().filter(j -> j.getNumeroJornada()==1).findFirst();
        return primeraJornda.map(Jornada::getFechaInicio).orElse(null);
    }

    public String getInicioStr(){
        return AESUtils.dateToString(getInicio());
    }

    public Date getFinalizacion(){
        return  getUltimaJornada().getFechaFin();
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

    // desde la vista
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

    public boolean inscripcionesEstanAbiertas() {
        return getLimiteInscripciones().after(new Date());
    }

    @Override
    public int compareTo(Object o) {
        return ((Competicion) o).getLimiteInscripciones().compareTo(this.getLimiteInscripciones());
    }
}
