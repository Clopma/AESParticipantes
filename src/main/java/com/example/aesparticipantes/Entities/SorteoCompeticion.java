package com.example.aesparticipantes.Entities;

import com.example.aesparticipantes.Entities.Keys.SorteoCompeticionKey;
import lombok.*;

import javax.persistence.*;

@Table(name = "SorteoCompeticion")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(SorteoCompeticionKey.class)
@Getter
@Setter
public class SorteoCompeticion {

    @Id
    @ManyToOne
    private Sorteo sorteo; //Importante EAGER

    @Id
    @ManyToOne
    private Competicion competicion; //Importante EAGER

    private int orden;


}
