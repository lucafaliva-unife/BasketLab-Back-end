package it.unife.basketlab.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Train {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID idx_train;

    private UUID id_player;

    private Double percentuale_tiri;

    private Double tempo_corsa;

}
