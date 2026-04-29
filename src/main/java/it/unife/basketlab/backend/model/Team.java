package it.unife.basketlab.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id_team;

    private String nome;

    private String citta;

}
