package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Utils.AESUtils;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Table(name = "Participantes")
@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participante implements Comparable {


    @Id
    @Column(length = 75)
    private String nombre;

    @Column(unique = true)
    private String wcaId;

    private String linkPerfilWCA;
    private String nombreWCA;
    private String gender;
    private String pais;
    private Date fechaCreacionWCA;
    private Date fechaActualicazionWCA;
    private String urlImagenPerfil;
    private String urlImagenPerfilIcono;

    @Column(columnDefinition = "boolean default false")
    private boolean confirmado;

    @OneToMany(mappedBy = "participante")
    List<Tiempo> tiempos;



    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Participante)) {
            return false;
        }

        Participante p = (Participante) o;

        return p.getNombre().equals(this.getNombre());
    }

    @Override
    public int compareTo(Object o) {
        return  AESUtils.COLLATOR.compare(this.getNombre(), ((Participante) o).getNombre());
    }
}
