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
public class Train {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID idx_train;

    @NotBlank
    private UUID id_player;

    @NotNull
    private Double percentuale_tiri;

    @NotNull
    private Double tempo_corsa;

}
