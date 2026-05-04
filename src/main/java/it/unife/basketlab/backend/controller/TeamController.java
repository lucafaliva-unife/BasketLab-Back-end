package it.unife.basketlab.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import it.unife.basketlab.backend.DTO.AnalyticsDTO;
import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Team;
import it.unife.basketlab.backend.service.PlayerService;
import it.unife.basketlab.backend.service.TeamService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    private String svincolatiTeamName= "Svincolati";

    /*
    Ritorna tutti i team salvati nel DB in ordine di scrittura.
    Se non ci sono team sul DB ritorna una lista vuota.
    Il codice di ritorno è sempre "200 OK".
    */
    @GetMapping
    public List<Team> getTeams() {
        return teamService.getTeams();
    }

    /*
    Ritorna il team con ID specificato.
    Il codice di ritorno è:
        -> "200 OK" se il team esiste;
        -> "404 Not Found" se il team non esiste;
    */
    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable UUID id) {
        Optional<Team> team= teamService.getTeamById(id);
        return team.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
    Ritorna tutti i team ordinati per perfomance di allenamento dei suoi giocatori in ordine decrescente.
    Include in fondo alla lista anche i team senza allenamenti.
    Se non ci sono team sul DB ritorna una lista vuota.
    Il codice di ritorno è sempre "200 OK".
    */
    @GetMapping("/ranking")
    public List<Team> getTeamsRanking() {
        return teamService.getTeamsRanking();
    }

    /*
    Crea un nuovo team nel DB dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> essersi assicurato che non si vuole creare un secondo team "Svincolati" (ce ne deve essere solo uno sul DB);
        -> essersi assicurato che non esista già un team con lo stesso nome di quello nuovo;
    Il codice di ritorno è:
        -> "201 Created" se il team supera i controlli e viene creato correttamente;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi (not blank);
        -> "409 Conflict" se:
            -> il team passato porta il nome di un team che esiste già;
            -> il team passato si chiama "Svincolati";
    */
    @PostMapping
    public ResponseEntity<Void> createTeam(@RequestBody @Valid Team team) {
        if(teamService.teamExistsByName(team.getNome()) || team.getNome().equals(svincolatiTeamName)) {
            return ResponseEntity.status(409).build();
        }
        team.setId_team(null); // L'ID viene reso nullo per assicurarsi di non sovrascrivere un team esistente, nel caso in cui il client invii un ID.
        teamService.saveTeam(team);
        return ResponseEntity.status(201).build();
    }

    /*
    Modifica un team esistente dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> essersi assicurato che le modifiche non rinominino in "Svincolati" un team che non è effettivamente quello degli svincolati;
        -> essersi assicurato che le modifiche non riguardino il team degli svincolati (check sull'ID);
        -> essersi assicurato che l'ID del team da modificare esista;
        -> essersi assicurato che le modifiche non rinominino un team usando il nome di un altro team già esistente sul DB;
    Il codice di ritorno è:
        -> "204 No Content" se vengono superati tutti i controlli ed il team viene modificato correttamente;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi (not blank);
        -> "404 Not Found" se l'ID passato non esiste;
        -> "409 Conflict" se:
            -> il team passato porta il nome di un team che esiste già;
            -> l'ID passato è l'ID del team "Svincolati";
            -> il team passato porta il nome "Svincolati";
    */
    @PutMapping("/{id}")
    public ResponseEntity<Void> editTeamById(@PathVariable UUID id, @RequestBody @Valid Team team) {
        if(team.getNome().equals("Svincolati") || team.getId_team().equals(teamService.getTeamSvincolati().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        if(!teamService.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if(teamService.teamExistsByNameExcludeId(team.getNome(), id)) {
            return ResponseEntity.status(409).build();
        }
        team.setId_team(id); // L'ID viene forzato ad essere quello specificato nel parametro, così se il client lo include nell'oggetto "team" non si rischia di modificare il team sbagliato o peggio di crearne uno nuovo.
        teamService.saveTeam(team);
        return ResponseEntity.noContent().build();
    }

    /*
    Elimina dal DB un team esistente dopo:
        -> essersi assicurato che non si intenda eliminare il team degli svincolati (check sull'ID);
        -> essersi assicurato che l'ID passato esista;
    Prima di eliminare il team sposta tutti i suoi giocatori nel team "Svincolati".
    Il codice di ritorno è:
        -> "204 No Content" se vengono passati tutti i controlli ed il team viene modificato correttamente;
        -> "404 Not Found" se l'ID passato non esiste;
        -> "409 Conflict" se l'ID passato è l'ID del team degli svincolati;
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamById(@PathVariable UUID id) {
        if(id.equals(teamService.getTeamSvincolati().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        try {
            playerService.movePlayersToSvincolatiByTeamId(id);
            teamService.deleteTeamById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Ritorna un oggetto di tipo TeamAnalyticsDTO contenente le medie dei dati di allenamento per il team selezionato.
    Usiamo un DTO apposito e non un oggetto di tipo Train per forzare ad escludere i campi "id_player" e "idx_train".
    Se il team selezionato non ha allenamenti, viene ritornato un TeamAnalyticsDTO senza campi.
    È vietato chiedere i dati di allenamento del team "Svincolati" perché non ha senso.
    Occorre quindi verificare che:
        -> l'ID del team selezionato esista;
        -> l'ID del team selezionato non sia quello del team "Svincolati";
    Il codice di ritorno è:
        -> "200 OK" se vengono passati tutti i controlli e viene restituito il DTO;
        -> "404 Not Found" se l'ID passato non esiste;
        -> "409 Conflict" se l'ID passato è quello del team "Svincolati";
    */
    @GetMapping("/{id}/analytics")
    public ResponseEntity<AnalyticsDTO> getAnalyticsByTeamId(@PathVariable UUID id) {
        if(id.equals(teamService.getTeamSvincolati().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        if(!teamService.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(teamService.getAnalyticsByTeamId(id));
    }

    /*
    Ritorna tutti i giocatori del team selezionato ordinati per performance di allenamento in ordine decrescente.
    Include in fondo alla lista anche i giocatori del team senza allenamenti.
    Se non ci sono giocatori nel team allora ritorna una lista vuota.
    È vietato chiedere la classifica dei giocatori del team "Svincolati" perché non ha senso.
    Occorre prima verificare che:
        -> l'ID del team selezionato esista;
        -> l'ID del team selezionato non sia quello del team "Svincolati";
    Il codice di ritorno è:
        -> "200 OK" se vengono passati tutti i controlli e viene restituita la lista di giocatori;
        -> "404 Not Found" se l'ID passato non esiste;
        -> "409 Conflict" se l'ID passato è quello del team "Svincolati";
    */
    @GetMapping("/{id}/ranking")
    public ResponseEntity<List<Player>> getPlayersRankingByTeamId(@PathVariable UUID id) {
        if(id.equals(teamService.getTeamSvincolati().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        if(!teamService.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(teamService.getRankingByTeamId(id));
    }

    /*
    Ritorna tutti i giocatori del team selezionato ordinati per inserimento nel DB.
    Se non ci sono giocatori nel team allora ritorna una lista vuota.
    Occorre prima verificare che:
        -> l'ID del team selezionato esista;
    Il codice di ritorno è:
        -> "200 OK" se vengono passati tutti i controlli e viene restituita la lista di giocatori;
        -> "404 Not Found" se l'ID passato non esiste;
    */
    @GetMapping("/{id}/players")
    public ResponseEntity<List<Player>> getPlayersByTeamId(@PathVariable UUID id) {
        if(!teamService.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(playerService.getPlayersByTeamId(id));
    }

}