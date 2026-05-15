package it.unife.basketlab.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Team;
import it.unife.basketlab.backend.repository.PlayerRepository;
import it.unife.basketlab.backend.repository.TeamRepository;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository repository;

    @Autowired
    private TeamRepository teamRepository;

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public Optional<Player> getPlayerById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void movePlayersToSvincolatiByTeamId(UUID id) {
        // Trova il team "Svincolati" direttamente dal repository
        Team svincolatiTeam= teamRepository.findAll().stream()
            .filter(team -> team.getNome().equals("Svincolati"))
            .findFirst()
            .orElse(null);
        if(svincolatiTeam == null) {
            return; // Se il team Svincolati non esiste, non fare nulla
        }
        UUID svincolatiId= svincolatiTeam.getId_team();
        List<Player> players = getAllPlayers().stream()
            .filter(player -> id.equals(player.getId_team()))
            .peek(player -> player.setId_team(svincolatiId))
            .collect(Collectors.toList());
        repository.saveAll(players);
    }

    public List<Player> getPlayersByTeamId(UUID id) {
        List<Player> allPlayers= getAllPlayers();
        List<Player> filteredPlayers= new ArrayList<>();
        for(Player player : allPlayers) {
            if(player.getId_team().equals(id)) {
                filteredPlayers.add(player);
            }
        }
        return filteredPlayers;
    }

    public Player savePlayer(Player player) {
        return repository.save(player);
    }

    public boolean playerExistsById(UUID id) {
        return repository.existsById(id);
    }

    public void deletePlayerById(UUID id) {
        repository.deleteById(id);
    }

}
