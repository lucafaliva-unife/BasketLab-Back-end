package it.unife.basketlab.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unife.basketlab.backend.DTO.TeamAnalyticsDTO;
import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Team;
import it.unife.basketlab.backend.repository.TeamRepository;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    @Autowired
    private TeamRepository repository;

    private String svincolatiTeamName= "Svincolati";

    public List<Team> getTeams() {
        return repository.findAll();
    }

    public Optional<Team> getTeamById(UUID id) {
        return repository.findById(id);
    }

    public Team saveTeam(Team team) {
        return repository.save(team);
    }

    public void deleteTeamById(UUID id) {
        repository.deleteById(id);
    }

    public boolean teamExistsById(UUID id) {
        return repository.existsById(id);
    }

    public boolean teamExistsByName(String name) {
        List<Team> teams= getTeams();
        for(Team team : teams) {
            if(team.getNome().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Team getTeamByName(String name) {
        List<Team> teams= getTeams();
        for(Team team : teams) {
            if(team.getNome().equals(svincolatiTeamName)) {
                return team;
            }
        }
        return null;
    }

    public boolean teamExistsByNameExcludeId(String name, UUID id) {
        List<Team> teams= getTeams();
        for(Team team : teams) {
            if(team.getNome().equals(name) && !team.getId_team().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public TeamAnalyticsDTO getAnalyticsByTeamId(UUID id) {
        TeamAnalyticsDTO analytics= repository.getAnalyticsByTeamId(id);
        if(analytics == null) {
            return new TeamAnalyticsDTO();
        } else {
            return analytics;
        }
    }

    public List<Player> getRankingByTeamId(UUID id) {
        List<Player> ranking= repository.getPlayersRankingByTeamId(id);
        if(ranking == null) {
            return Collections.emptyList();
        } else {
            return ranking;
        }
    }

    private void createSvincolati() {
        if(!teamExistsByName(svincolatiTeamName)) {
            Team svincolati= new Team();
            svincolati.setNome(svincolatiTeamName);
            svincolati.setCitta("/");
            repository.save(svincolati);
        }
    }

    @PostConstruct
    public void init() {
        createSvincolati();
    }

    public Team getTeamSvincolati() {
        Team svincolati= getTeamByName(svincolatiTeamName);
        if(svincolati == null) {
            createSvincolati();
            svincolati= getTeamSvincolati();
        }
        return svincolati;
    }

    public List<Team> getTeamsRanking() {
        return repository.getTeamsRanking();
    }

}