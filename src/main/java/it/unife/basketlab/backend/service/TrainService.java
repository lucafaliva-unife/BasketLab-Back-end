package it.unife.basketlab.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unife.basketlab.backend.model.Train;
import it.unife.basketlab.backend.repository.TrainRepository;

@Service
public class TrainService {
    
    @Autowired
    private TrainRepository repository;

    public List<Train> getTrains() {
        return repository.findAll();
    }

    public List<Train> getTrainsByPlayerId(UUID id) {
        List<Train> allTrains= repository.findAll();
        List<Train> trainsFiltered= new ArrayList<>();
        for(Train train : allTrains) {
            if(train.getId_player().equals(id)) {
                trainsFiltered.add(train);
            }
        }
        return trainsFiltered;
    }

    public void saveTrain(Train train) {
        repository.save(train);
    }

}
