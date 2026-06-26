package it.unife.basketlab.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unife.basketlab.backend.model.Match;
import it.unife.basketlab.backend.repository.MatchRepository;

@Service
public class MatchService {
    
    @Autowired
    private MatchRepository repository;

    /*
    Ritorna l'elenco di tutti i match.
    */
    public List<Match> getAllMatches() {
        return repository.findAll();
    }

    /*
    Ritorna il match che ha come ID l'ID passato.
    */
    public Optional<Match> getMatchById(UUID id) {
        return repository.findById(id);
    }

    /*
    Salva un nuovo match se l'ID del match passato non è già presente, altrimenti modifica quello già presente.
    Infine ritorna la risposta HTTP.
    */
    public Match saveMatch(Match match) {
        return repository.save(match);
    }

    /*
    Ritorna un booleano che indica se esiste un match che ha come ID l'ID passato.
    */
    public boolean matchExistsById(UUID id) {
        return repository.existsById(id);
    }

    /*
    Elimina il match che ha come ID l'ID passato e ritorna la risposta HTTP.
    */
    public void deleteMatchById(UUID id) {
        repository.deleteById(id);
    }

    /*
    Elimina tutti i match che hanno come ID del team in casa o in trasferta l'ID passato.
    */
    public void deleteMatchesByTeamId(UUID id) {
        List<Match> allMatches= getAllMatches();
        for(Match m : allMatches) {
            if(m.getTeam_casa().equals(id) || m.getTeam_trasferta().equals(id)) {
                deleteMatchById(m.getId_match());
            }
        }
    }

}
