package it.unife.basketlab.backend.controller;

import java.util.List;
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

import it.unife.basketlab.backend.model.Match;
import it.unife.basketlab.backend.service.MatchService;
import it.unife.basketlab.backend.service.PlayerService;
import it.unife.basketlab.backend.service.TeamService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
    
    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    /*
    Ritorna tutti i match salvati nel DB in ordine di scrittura.
    Se non ci sono match sul DB ritorna una lista vuota.
    Il codice di ritorno è sempre "200 OK".
    */
    @GetMapping
    public List<Match> getMatch() {
        return matchService.getAllMatches();
    }

    /*
    Crea un nuovo match nel DB dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> aver verificato che gli ID dei due teams passati esistano;
        -> aver verificato che gli ID dei due teams passati siano diversi;
        -> aver verificato che nessuno degli ID dei due team sia quello degli svincolati;
        -> aver verificato che i punti dei due team non siano < 0 o > 150;
        -> aver verificato che il numero di players di entrambe le squadre sia almeno 5 a testa;
    Il codice di ritorno è:
        -> "201 Created" se i controlli vengono passati ed il match viene creato correttamente;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi (anche not blank e not null);
        -> "409 Conflict" se:
            -> l'ID di uno dei due team è l'ID del team degli svincolati;
            -> il numero di players di una delle due squadre è < 5.
    */
    @PostMapping
    public ResponseEntity<Void> createMatch(@RequestBody @Valid Match match) {
        if(!teamService.teamExistsById(match.getTeam_casa()) || ! teamService.teamExistsById(match.getTeam_trasferta())) {
            return ResponseEntity.badRequest().build();
        }
        if(match.getTeam_casa().equals(match.getTeam_trasferta())) {
            return ResponseEntity.badRequest().build();
        }
        if(match.getPunti_casa() < 0 || match.getPunti_casa() > 150) {
            return ResponseEntity.badRequest().build();
        }
        if(match.getPunti_trasferta() < 0 || match.getPunti_trasferta() > 150) {
            return ResponseEntity.badRequest().build();
        }
        if(teamService.getTeamSvincolati().getId_team().equals(match.getTeam_casa())) {
            return ResponseEntity.status(409).build();
        }
        if(teamService.getTeamSvincolati().getId_team().equals(match.getTeam_trasferta())) {
            return ResponseEntity.status(409).build();
        }
        if(playerService.getPlayersByTeamId(match.getTeam_casa()).size() < 5) {
            return ResponseEntity.status(409).build();
        }
        if(playerService.getPlayersByTeamId(match.getTeam_trasferta()).size() < 5) {
            return ResponseEntity.status(409).build();
        }
        match.setId_match(null); // L'ID viene reso nullo per assicurarsi di non sovrascrivere un match esistente, nel caso in cui il client invii un ID.
        matchService.saveMatch(match);
        return ResponseEntity.status(201).build();
    }

    /*
    Elimina dal DB un match esistente dopo essersi assicurato che l'ID passato esista;
    Il codice di ritorno è:
        -> "204 No Content" se vengono passati tutti i controlli ed il match viene modificato correttamente;
        -> "404 Not Found" se l'ID passato non esiste;
    */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatchById(@PathVariable UUID id) {
        try {
            matchService.deleteMatchById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Modifica un match esistente dopo:
        -> aver validato i campi dell'oggetto ricevuto;
        -> essersi assicurato che le modifiche non usino il team degli svincolati;
        -> essersi assicurato che gli ID dei due team esistano;
        -> essersi assicurato che l'ID del match esista;
        -> aver verificato che i punti dei due team non siano < 0 o > 150;
        -> aver verificato che il numero di players di entrambe le squadre sia almeno 5 a testa;
        -> aver verificato che gli ID dei due teams passati siano diversi;
    Il codice di ritorno è:
        -> "204 No Content" se vengono superati tutti i controlli ed il match viene modificato correttamente;
        -> "400 Bad Request" se l'oggetto passato non supera i controlli di validazione dei campi (anche not blank e not null);
        -> "404 Not Found" se l'ID del match non esiste;
        -> "409 Conflict" se:
            -> l'ID di uno dei due team è l'ID del team "Svincolati";
            -> uno dei due team ha un numero insufficiente di giocatori;
    */
    @PutMapping("/{id}")
    public ResponseEntity<Void> editMatchById(@PathVariable UUID id, @RequestBody @Valid Match match) {
        if(!teamService.teamExistsById(match.getTeam_casa()) || ! teamService.teamExistsById(match.getTeam_trasferta())) {
            return ResponseEntity.badRequest().build();
        }
        if(match.getTeam_casa().equals(match.getTeam_trasferta())) {
            return ResponseEntity.badRequest().build();
        }
        if(!matchService.matchExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if(match.getPunti_casa() < 0 || match.getPunti_casa() > 150) {
            return ResponseEntity.badRequest().build();
        }
        if(match.getPunti_trasferta() < 0 || match.getPunti_trasferta() > 150) {
            return ResponseEntity.badRequest().build();
        }
        if(teamService.getTeamSvincolati().getId_team().equals(match.getTeam_casa())) {
            return ResponseEntity.status(409).build();
        }
        if(teamService.getTeamSvincolati().getId_team().equals(match.getTeam_trasferta())) {
            return ResponseEntity.status(409).build();
        }
        if(playerService.getPlayersByTeamId(match.getTeam_casa()).size() < 5) {
            return ResponseEntity.status(409).build();
        }
        if(playerService.getPlayersByTeamId(match.getTeam_trasferta()).size() < 5) {
            return ResponseEntity.status(409).build();
        }
        match.setId_match(id); // L'ID viene forzato ad essere quello specificato nel parametro, così se il client lo include nell'oggetto "match" non si rischia di modificare il match sbagliato o peggio di crearne uno nuovo.
        matchService.saveMatch(match);
        return ResponseEntity.noContent().build();
    }

}