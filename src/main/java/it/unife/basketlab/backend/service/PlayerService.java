package it.unife.basketlab.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    /*
    Ritorna l'elenco di tutti i giocatori.
    */
    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    /*
    Ritorna il giocatore che ha come ID l'ID passato.
    */
    public Optional<Player> getPlayerById(UUID id) {
        return repository.findById(id);
    }

    /*
    Sposta i giocatori del team che ha come ID l'ID passato verso il team degli svincolati.
    */
    @Transactional
    public void movePlayersToSvincolatiByTeamId(UUID id) {
        // Trova il team "Svincolati" direttamente dal repository
        Team svincolatiTeam= null;
        for(Team team : teamRepository.findAll()) {
            if(team.getNome().equals("Svincolati")) {
                svincolatiTeam= team;
                break;
            }
        }
        if(svincolatiTeam == null) {
            return; // Se il team Svincolati non esiste, non fare nulla
        }
        // Seleziona tutti i giocatori del team e modifica il loro Id_team nell'ID del team "Svincolati"
        List<Player> players= getPlayersByTeamId(id);
        for(Player player : players) {
            player.setId_team(svincolatiTeam.getId_team());
        }
        repository.saveAll(players);
    }

    /*
    Ritorna tutti i giocatori del team che ha come ID l'ID passato.
    */
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

    /*
    Salva un nuovo giocatore se l'ID del giocatore passato non è già presente, altrimenti modifica quello già presente.
    Infine ritorna la risposta HTTP.
    */
    public Player savePlayer(Player player) {
        return repository.save(player);
    }

    /*
    Ritorna un booleano che indica se esiste un giocatore che ha come ID l'ID passato.
    */
    public boolean playerExistsById(UUID id) {
        return repository.existsById(id);
    }

    /*
    Elimina il giocatore che ha come ID l'ID passato e ritorna la risposta HTTP.
    */
    public void deletePlayerById(UUID id) {
        repository.deleteById(id);
    }

}
