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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        List<Train> teamTrains= new ArrayList<Train>();
        // Filtro gli allenamenti relativi soltanto ai giocatori del team selezionato
        teamTrains= trainService.getTrainsByTeamId(teamId);
        // Se il team selezionato non possiede allenamenti allora ritorno un DTO vuoto
        if(teamTrains.isEmpty()) {
            return new AnalyticsDTO(null, null);
        }
        // Se il team ha almeno un allenamento, calcolo la media del tempo corsa e la media della % tiri
        Double percentualeTiri= Double.valueOf(0);
        Double tempoCorsa= Double.valueOf(0);
        for(Train t : teamTrains) {
            percentualeTiri+= t.getPercentuale_tiri();
            tempoCorsa+= t.getTempo_corsa();
        }
        percentualeTiri/= teamTrains.size();
        tempoCorsa/= teamTrains.size();
        return new AnalyticsDTO(tempoCorsa, percentualeTiri);
    }

    /*
    Restituisce la classifica dei giocatori del team richiesto.
    Per ogni giocatore:
      - usa gli allenamenti del giocatore per calcolare il punteggio di performance;
      - mette prima chi ha almeno un allenamento e dopo chi non ne ha;
      - ordina chi ha allenamenti in base al punteggio totale (decrescente);
    */
    public List<Player> getRankingByTeamId(UUID teamId) {
        List<Player> teamPlayers= playerService.getPlayersByTeamId(teamId);
        List<Player> ranking= new ArrayList<>(teamPlayers);
        ranking.sort((p1, p2) -> {
            List<Train> p1Trains= trainService.getTrainsByPlayerId(p1.getId_player());
            List<Train> p2Trains= trainService.getTrainsByPlayerId(p2.getId_player());
            // Metto prima i giocatori con allenamenti
            boolean p1HasTrain= !p1Trains.isEmpty();
            boolean p2HasTrain= !p2Trains.isEmpty();
            if(p1HasTrain != p2HasTrain) {
                return p1HasTrain ? -1 : 1;
            }
            // Se entrambi hanno allenamenti, confronto il punteggio di performance
            Double p1Score= getPerformanceScore(p1Trains);
            Double p2Score= getPerformanceScore(p2Trains);
            return p2Score.compareTo(p1Score);
        });
        return ranking;
    }

    /*
    Restituisce l'elenco di tutti i team ordinati in base alle performance di allenamento calcolate secondo il metodo
    getPerformanceScore() (ordine decrescente, dal più prestante al meno prestante).
    Include anche i team senza allenamenti ma li inserisce in fondo alla classifica, mantenendo l'ordine originale.
    Mette sempre per ultimo il team degli svincolati.
    */
    public List<Team> getTeamsRanking() {
        // Raccolgo tutti i team, tranne gli svincolati
        List<Team> allTeams= new ArrayList<>();
        for(Team team : getTeams()) {
            if(!team.getNome().equals(svincolatiTeamName)) {
                allTeams.add(team);
            }
        }
        // Copio la lista di tutti i team (senza "Svincolati" e la ordino per performance)
        List<Team> ranking= new ArrayList<>(allTeams);
        ranking.sort((t1, t2) -> {
            List<Train> t1Trains= trainService.getTrainsByTeamId(t1.getId_team());
            List<Train> t2Trains= trainService.getTrainsByTeamId(t2.getId_team());
            // Faccio andare in fondo alla classifica i team senza allenamenti
            boolean t1HasTrain= !t1Trains.isEmpty();
            boolean t2HasTrain= !t2Trains.isEmpty();
            if(t1HasTrain != t2HasTrain) {
                return t1HasTrain ? -1 : 1;
            }
            // Definisco la regola per ordinare i team
            Double t1Score= getPerformanceScore(t1Trains);
            Double t2Score= getPerformanceScore(t2Trains);
            return t2Score.compareTo(t1Score);
        });
        // Inserisco alla fine della classifica il team degli svincolati
        ranking.add(getTeamSvincolati());
        return ranking;
    }

    /*
    Data una lista di allenamenti, esegue i seguenti passaggi sugli elementi:
    1) calcola la media complessiva della % tiri e del tempo corsa invertito (1 / tempo corsa).
       Il risultato ottenuto è un insieme di due numeri: % tiri media e tempo corsa media (invertita);
    2) dati i due numeri appena calcolati, calcola la media dei due valori con:
       (% tiri media + tempo corsa media invertita) / 2.
       Il risultato ottenuto è un numero reale (Double).
    */
    private Double getPerformanceScore(List<Train> trains) {
        // Se la lista di allenamenti è vuota allora ritorno 0.0
        if(trains.isEmpty()) {
            return 0.0;
        }
        // Calcolo le performance e ritorno il valore risultante
        double percentualeTiriSum= 0.0;
        double invertedTempoSum= 0.0;
        for (Train t : trains) {
            percentualeTiriSum += t.getPercentuale_tiri();
            invertedTempoSum += 1.0 / t.getTempo_corsa();
        }
        double avgPercentualeTiri= percentualeTiriSum / trains.size();
        double avgInvertedTempo= invertedTempoSum / trains.size();
        return (avgPercentualeTiri + avgInvertedTempo) / 2.0;
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