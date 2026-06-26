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
public class Matches {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id_match;

    @NotNull
    private UUID team_casa;

    @NotNull
    private UUID team_trasferta;

    @NotNull
    private Integer punti_casa;

    @NotNull
    private Integer punti_trasferta;

    @NotBlank
    private String data;

}