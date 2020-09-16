package com.example.miwebbase.Entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = Categoria.T_CATEGORIAS)
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    public static final String T_CATEGORIAS = "Categorias";

    @Id
    private String nombre;

    @NotNull
    private int numTiempos;

    @NotNull
    private int orden;

    private Integer cortePlayOffs;


}
