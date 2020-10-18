package com.example.miwebbase.Entities;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "Competiciones")
@Entity
@Getter
public class Competicion {

    @Id
    @Column(length = 25)
    private String nombre;

    private int numJornadas;


}
