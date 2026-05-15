package it.unife.basketlab.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unife.basketlab.backend.DTO.AnalyticsDTO;
import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Team;
import it.unife.basketlab.backend.model.Train;
import it.unife.basketlab.backend.repository.TeamRepository;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository repository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TrainService trainService;

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
            if(team.getNome().equals(name)) {
                return team;
            }
        }
        return null;
    }

    /*
    Funziona come teamExistsByName() ma permette di escludere un ID dalla ricerca.
    */
    public boolean teamExistsByNameExcludeId(String name, UUID id) {
        List<Team> teams= getTeams();
        for(Team team : teams) {
            if(team.getNome().equals(name) && !team.getId_team().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public AnalyticsDTO getAnalyticsByTeamId(UUID teamId) {
        List<Player> teamPlayers= playerService.getPlayersByTeamId(teamId);
        List<Train> allTrains= trainService.getTrains();
        List<Train> teamTrains= allTrains.stream()
            .filter(train -> teamPlayers.stream()
                .anyMatch(player -> player.getId_player().equals(train.getId_player())))
            .collect(Collectors.toList());
        
        if(teamTrains.isEmpty()) {
            return new AnalyticsDTO(null, null);
        }
        Double avgTempCorsa= teamTrains.stream()
            .mapToDouble(Train::getTempo_corsa)
            .average()
            .orElse(0.0);
            
        Double avgPercentualeTiri= teamTrains.stream()
            .mapToDouble(Train::getPercentuale_tiri)
            .average()
            .orElse(0.0);
        return new AnalyticsDTO(avgTempCorsa, avgPercentualeTiri);
    }

    public List<Player> getRankingByTeamId(UUID teamId) {
        List<Player> teamPlayers= playerService.getPlayersByTeamId(teamId);
        List<Train> allTrains= trainService.getTrains();
        return teamPlayers.stream()
            .sorted((p1, p2) -> {
                List<Train> p1Trains = allTrains.stream()
                    .filter(train -> train.getId_player().equals(p1.getId_player()))
                    .collect(Collectors.toList());
                List<Train> p2Trains = allTrains.stream()
                    .filter(train -> train.getId_player().equals(p2.getId_player()))
                    .collect(Collectors.toList());
                // Players with training first
                if((p1Trains.isEmpty() ? 0 : 1) != (p2Trains.isEmpty() ? 0 : 1)) {
                    return (p2Trains.isEmpty() ? 0 : 1) - (p1Trains.isEmpty() ? 0 : 1);
                }
                // Sort by performance score (avg percentuale_tiri + avg 1/tempo_corsa) / 2
                Double p1Score = p1Trains.isEmpty() ? 0.0 : 
                    (p1Trains.stream().mapToDouble(Train::getPercentuale_tiri).average().orElse(0.0) +
                     p1Trains.stream().mapToDouble(t -> 1.0 / t.getTempo_corsa()).average().orElse(0.0)) / 2;    
                Double p2Score = p2Trains.isEmpty() ? 0.0 :
                    (p2Trains.stream().mapToDouble(Train::getPercentuale_tiri).average().orElse(0.0) +
                     p2Trains.stream().mapToDouble(t -> 1.0 / t.getTempo_corsa()).average().orElse(0.0)) / 2;
                return p2Score.compareTo(p1Score);
            })
            .collect(Collectors.toList());
    }

    public List<Team> getTeamsRanking() {
        List<Team> allTeams= getTeams().stream()
            .filter(team -> !team.getNome().equals(svincolatiTeamName))
            .collect(Collectors.toList());
        List<Player> allPlayers= playerService.getAllPlayers();
        List<Train> allTrains= trainService.getTrains();
        return allTeams.stream()
            .sorted((t1, t2) -> {
                List<Train> t1Trains= allTrains.stream()
                    .filter(train -> allPlayers.stream()
                        .filter(p -> p.getId_team().equals(t1.getId_team()))
                        .anyMatch(p -> p.getId_player().equals(train.getId_player())))
                    .collect(Collectors.toList());
                List<Train> t2Trains= allTrains.stream()
                    .filter(train -> allPlayers.stream()
                        .filter(p -> p.getId_team().equals(t2.getId_team()))
                        .anyMatch(p -> p.getId_player().equals(train.getId_player())))
                    .collect(Collectors.toList());
                // Teams with training first
                if((t1Trains.isEmpty() ? 0 : 1) != (t2Trains.isEmpty() ? 0 : 1)) {
                    return (t2Trains.isEmpty() ? 0 : 1) - (t1Trains.isEmpty() ? 0 : 1);
                }
                // Sort by performance score
                Double t1Score = t1Trains.isEmpty() ? 0.0 :
                    (t1Trains.stream().mapToDouble(Train::getPercentuale_tiri).average().orElse(0.0) +
                     t1Trains.stream().mapToDouble(t -> 1.0 / t.getTempo_corsa()).average().orElse(0.0)) / 2;
                     
                Double t2Score = t2Trains.isEmpty() ? 0.0 :
                    (t2Trains.stream().mapToDouble(Train::getPercentuale_tiri).average().orElse(0.0) +
                     t2Trains.stream().mapToDouble(t -> 1.0 / t.getTempo_corsa()).average().orElse(0.0)) / 2;
                return t2Score.compareTo(t1Score);
            })
            .collect(Collectors.toList());
    }

    /*
    Crea il team degli svincolati solo se non esiste già un altro team che si chiama "Svincolati".
    */
    private void createSvincolati() {
        if(!teamExistsByName(svincolatiTeamName)) {
            Team svincolati= new Team();
            svincolati.setNome(svincolatiTeamName);
            svincolati.setCitta("/");
            repository.save(svincolati);
        }
    }

    /*
    Il DB viene inizializzato con il team degli svincolati.
    */
    @PostConstruct
    public void init() {
        createSvincolati();
    }

    /*
    Ritorna il team degli svincolati se esiste, altrimenti se non esiste lo crea ed infine lo ritorna.
    */
    public Team getTeamSvincolati() {
        Team svincolati= getTeamByName(svincolatiTeamName);
        if(svincolati == null) {
            createSvincolati();
            svincolati= getTeamSvincolati();
        }
        return svincolati;
    }

}