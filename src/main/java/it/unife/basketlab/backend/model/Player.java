package it.unife.basketlab.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id_player;

    private String nome;

    private String cognome;

    private String ruolo;

    private float peso;

    private float altezza;

    private UUID id_team;

}
