package it.unife.basketlab.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.unife.basketlab.backend.model.Matches;

import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Matches, UUID> {
}
