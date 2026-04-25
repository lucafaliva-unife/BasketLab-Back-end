package it.unife.basketlab.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.unife.basketlab.backend.DTO.TeamAnalyticsDTO;
import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Team;
import it.unife.basketlab.backend.service.TeamService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService service;

    @GetMapping
    public List<Team> getTeams() {
        return service.getTeams();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getById(@PathVariable UUID id) {
        Optional<Team> team= service.getTeamById(id);
        return team.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Team team) {
        team.setId_team(null);
        service.saveTeam(team);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editTeamById(@PathVariable UUID id, @RequestBody Team team) {
        if(!service.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        team.setId_team(id);
        service.saveTeam(team);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            service.deleteTeamById(id);
            return ResponseEntity.noContent().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/analytics")
    public ResponseEntity<TeamAnalyticsDTO> getAnalyticsByTeamId(@PathVariable UUID id) {
        if(!service.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.getAnalyticsByTeamId(id));
    }

    @GetMapping("/{id}/ranking")
    public ResponseEntity<List<Player>> getRankingByTeamId(@PathVariable UUID id) {
        if(!service.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.getRankingByTeamId(id));
    }

}