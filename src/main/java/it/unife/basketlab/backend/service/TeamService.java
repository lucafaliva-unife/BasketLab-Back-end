package it.unife.basketlab.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unife.basketlab.backend.DTO.TeamAnalyticsDTO;
import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Team;
import it.unife.basketlab.backend.repository.TeamRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    @Autowired
    private TeamRepository repository;

    public List<Team> getTeams() {
        return repository.findAll();
    }

    public Optional<Team> getTeamById(UUID id) {
        return repository.findById(id);
    }

    public Team saveTeam(Team entity) {
        return repository.save(entity);
    }

    public void deleteTeamById(UUID id) {
        repository.deleteById(id);
    }

    public boolean teamExistsById(UUID id) {
        return repository.existsById(id);
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
        List<Player> ranking= repository.getRankingByTeamId(id);
        if(ranking == null) {
            return Collections.emptyList();
        } else {
            return ranking;
        }
    }

}