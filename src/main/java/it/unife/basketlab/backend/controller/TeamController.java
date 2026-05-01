package it.unife.basketlab.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.unife.basketlab.backend.DTO.TeamAnalyticsDTO;
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

    @GetMapping
    public List<Team> getTeams() {
        return teamService.getTeams();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable UUID id) {
        Optional<Team> team= teamService.getTeamById(id);
        return team.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ranking")
    public List<Team> getTeamsRanking() {
        return teamService.getTeamsRanking();
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Team team) {
        if(teamService.teamExistsByName(team.getNome()) || team.getNome().equals(svincolatiTeamName)) {
            return ResponseEntity.status(409).build();
        }
        if(team.getCitta() == null || team.getNome() == null) {
            return ResponseEntity.status(409).build();
        }
        team.setId_team(null);
        teamService.saveTeam(team);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editTeamById(@PathVariable UUID id, @RequestBody Team team) {
        if(team.getNome().equals("Svincolati") || team.getId_team().equals(teamService.getTeamSvincolati().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        if(!teamService.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if(teamService.teamExistsByNameExcludeId(team.getNome(), id)) {
            return ResponseEntity.status(409).build();
        }
        if(team.getCitta() == null || team.getNome() == null) {
            return ResponseEntity.status(409).build();
        }
        team.setId_team(id);
        teamService.saveTeam(team);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
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

    @GetMapping("/{id}/analytics")
    public ResponseEntity<TeamAnalyticsDTO> getAnalyticsByTeamId(@PathVariable UUID id) {
        if(id.equals(teamService.getTeamSvincolati().getId_team())) {
            return ResponseEntity.status(409).build();
        }
        if(!teamService.teamExistsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(teamService.getAnalyticsByTeamId(id));
    }

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

}