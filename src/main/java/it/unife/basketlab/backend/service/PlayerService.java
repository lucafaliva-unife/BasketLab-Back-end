package it.unife.basketlab.backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.repository.PlayerRepository;

@Service
public class PlayerService {
    
    @Autowired
    private PlayerRepository repository;

    @Autowired
    private TeamService teams;

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    @Transactional
    public void movePlayersToSvincolatiByTeamId(UUID id) {
        UUID svincolatiId= teams.getTeamSvincolati().getId_team();
        List<Player> players = getAllPlayers().stream()
        .filter(player -> id.equals(player.getId_team()))
        .peek(player -> player.setId_team(svincolatiId))
        .collect(Collectors.toList());
        repository.saveAll(players);
    }

}
