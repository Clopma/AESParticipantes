package com.example.miwebbase.Entities;


import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = Categoria.T_CATEGORIAS)
@Entity
@Getter
public class Categoria {

    public static final String T_CATEGORIAS = "Categorias";

    @Id
    private String nombre;

    private int numTiempos;

    private int posicion;
}
