package it.unife.basketlab.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Train;
import it.unife.basketlab.backend.repository.TrainRepository;

@Service
public class TrainService {
    
    @Autowired
    private TrainRepository repository;

    @Autowired
    private PlayerService playerService;

    /*
    Ritorna la lista di tutti gli allenamenti eseguiti da tutti i giocatori.
    */
    public List<Train> getTrains() {
        return repository.findAll();
    }

    /*
    Ritorna tutti gli allenamenti eseguiti dal giocatore che ha come ID l'ID passato.
    */
    public List<Train> getTrainsByPlayerId(UUID id) {
        // Recupero tutti gli allenamenti
        List<Train> allTrains= repository.findAll();
        // Filtro e restituisco gli allenamenti del player in questione
        List<Train> trainsFiltered= new ArrayList<>();
        for(Train train : allTrains) {
            if(train.getId_player().equals(id)) {
                trainsFiltered.add(train);
            }
        }
        return trainsFiltered;
    }

    /*
    Ritorna tutti gli allenamenti eseguiti dai giocatori facenti parte del team che ha come ID l'ID passato.
    */
    public List<Train> getTrainsByTeamId(UUID id) {
        List<Train> teamTrains= new ArrayList<>();
        // Itero su tutti i giocatori del team
        for(Player p : playerService.getPlayersByTeamId(id)) {
            // Itero su tutti gli allenamenti del giocatore
            for(Train t : getTrainsByPlayerId(p.getId_player())) {
                // Raccolgo l'allenamento
                teamTrains.add(t);
            }
        }
        // Ritorno la lista di tutti gli allenamenti del team
        return teamTrains;
    }

    /*
    Salva un nuovo allenamento oppure ne sovrascrive uno se l'ID dell'allenamento passato è già presente.
    */
    public void saveTrain(Train train) {
        repository.save(train);
    }

}
