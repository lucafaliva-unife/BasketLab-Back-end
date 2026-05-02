package it.unife.basketlab.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id_player;

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotBlank
    private String ruolo;

    @NotNull
    private float peso;

    @NotNull
    private float altezza;

    @NotBlank
    private UUID id_team;

}
