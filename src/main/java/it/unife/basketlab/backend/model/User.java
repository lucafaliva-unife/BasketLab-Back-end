package it.unife.basketlab.backend.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id_user;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String type;

}