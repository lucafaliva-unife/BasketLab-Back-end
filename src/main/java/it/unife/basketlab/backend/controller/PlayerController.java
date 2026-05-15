package it.unife.basketlab.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Train;
import it.unife.basketlab.backend.service.PlayerService;
import it.unife.basketlab.backend.service.TeamService;
import it.unife.basketlab.backend.service.TrainService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    
    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TrainService trainService;

    /*
    Crea un nuovo giocatore nel DB dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> aver verificato che l'ID del team all'interno del giocatore passato esista;
        -> aver verificato che l'ID del team all'interno del giocatore passato non sia l'ID del team degli svincolati;
    Il codice di ritorno è:
        -> "201 Created" se i controlli vengono passati ed il giocatore viene creato correttamente;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi (not blank e not null);
        -> "409 Conflict" se l'ID del team all'interno del giocatore passato è l'ID del team degli svincolati;
    */
    @PostMapping
    public ResponseEntity<Void> createPlayer(@RequestBody @Valid Player player) {
        if(player.getId_team().equals(teamService.getTeamSvincolati().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        player.setId_player(null); // L'ID viene reso nullo per assicurarsi di non sovrascrivere un giocatore esistente, nel caso in cui il client invii un ID.
        playerService.savePlayer(player);
        return ResponseEntity.status(201).build();
    }

    /*
    Ritorna il giocatore con ID specificato.
    Il codice di ritorno è:
        -> "200 OK" se il giocatore esiste;
        -> "404 Not Found" se il giocatore non esiste;
    */
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable UUID id) {
        Optional<Player> player= playerService.getPlayerById(id);
        return player.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
    Modifica un giocatore esistente dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> essersi assicurato che l'ID del giocatore da modificare esista;
        -> essersi assicurato che l'ID del team del giocatore esista;
    Il codice di ritorno è:
        -> "204 No Content" se vengono superati tutti i controlli ed il team viene modificato correttamente;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi (not blank e not null);
        -> "404 Not Found" se l'ID passato non esiste;
        -> "409 Conflict" se l'ID del team del giocatore non esiste;
    */
    @PutMapping("/{id}")
    public ResponseEntity<Void> editPlayerById(@PathVariable UUID id, @RequestBody @Valid Player player) {
        if(!playerService.playerExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if(!teamService.teamExistsById(player.getId_team())) {
            return ResponseEntity.status(409).build();
        }
        player.setId_player(id); // L'ID viene forzato ad essere quello specificato nel parametro, così se il client lo include nell'oggetto "player" non si rischia di modificare il player sbagliato o peggio di crearne uno nuovo.
        playerService.savePlayer(player);
        return ResponseEntity.noContent().build();
    }

    /*
    Elimina dal DB un giocatore esistente dopo:
        -> essersi assicurato che l'ID passato esista;
    Il codice di ritorno è:
        -> "204 No Content" se vengono passati i controlli ed il giocatore viene modificato correttamente;
        -> "404 Not Found" se l'ID passato non esiste;
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerById(@PathVariable UUID id) {
        try {
            playerService.deletePlayerById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Restituisce tutti i dati di allenamento di un giocatore dopo:
        -> aver controllato che l'ID esista;
    Il codice di ritorno è:
        -> "200 OK" se vengono passati i controlli e vengono restituiti i dati di allenamento;
        -> "404 Not Found" se l'ID non esiste;
    */
    @GetMapping("/{id}/train")
    public ResponseEntity<List<Train>> getTrainsByPlayerId(@PathVariable UUID id) {
        if(!playerService.playerExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(trainService.getTrainsByPlayerId(id));
    }

    /*
    Crea un nuovo allenamento per un giocatore esistente sul DB dopo:
        -> aver controllato che l'ID del giocatore esista;
        -> aver validato i campi dell'oggetto ricevuto;
        -> essersi assicurato che il giocatore da allenare non sia svincolato;
    Il codice di ritorno è:
        -> "201 OK" se vengono passati i controlli e viene creato correttamente l'allenamento;
        -> "404 Not Found" se l'ID del giocatore non esiste;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi (not null);
        -> "409 Conflict" se il giocatore da allenare è svincolato;
    */
    @PostMapping("/{id}/train")
    public ResponseEntity<Void> trainPlayerById(@PathVariable UUID id, @RequestBody @Valid Train train) {
        if(!playerService.playerExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if(teamService.getTeamSvincolati().getId_team().equals(playerService.getPlayerById(id).get().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        train.setId_player(id); // L'ID viene forzato ad essere quello specificato nel parametro, così se il client lo include nell'oggetto "train" non si rischia di modificare un allenamento vecchio.
        trainService.saveTrain(train);
        return ResponseEntity.status(201).build();
    }

}