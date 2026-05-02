package it.unife.basketlab.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id_team;

    @NotBlank
    private String nome;

    @NotBlank
    private String citta;

}
