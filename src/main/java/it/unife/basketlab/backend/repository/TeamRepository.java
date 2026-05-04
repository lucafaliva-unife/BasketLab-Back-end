package it.unife.basketlab.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.unife.basketlab.backend.DTO.AnalyticsDTO;
import it.unife.basketlab.backend.model.Player;
import it.unife.basketlab.backend.model.Team;

import java.util.List;
import java.util.UUID;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    @Query("SELECT new it.unife.basketlab.backend.DTO.TeamAnalyticsDTO(AVG(t.tempo_corsa), AVG(t.percentuale_tiri)) " +
            "FROM Train t, Player p " +
            "WHERE t.id_player = p.id_player AND p.id_team = :teamId")
    AnalyticsDTO getAnalyticsByTeamId(UUID teamId);

    @Query("SELECT p FROM Player p LEFT JOIN Train t ON t.id_player = p.id_player " +
            "WHERE p.id_team = :teamId " +
            "GROUP BY p.id_player " +
            "ORDER BY CASE WHEN COUNT(t) > 0 THEN 0 ELSE 1 END ASC, " +
            "COALESCE((AVG(t.percentuale_tiri) + AVG(1.0 / t.tempo_corsa)) / 2, 0) DESC")
    List<Player> getPlayersRankingByTeamId(UUID teamId);

    @Query("SELECT t FROM Team t " +
            "LEFT JOIN Player p ON p.id_team = t.id_team " +
            "LEFT JOIN Train tr ON tr.id_player = p.id_player " +
            "WHERE t.nome <> 'Svincolati' " +
            "GROUP BY t.id_team " +
            "ORDER BY CASE WHEN COUNT(tr) > 0 THEN 0 ELSE 1 END ASC, " +
            "COALESCE((AVG(tr.percentuale_tiri) + AVG(1.0 / tr.tempo_corsa)) / 2, 0) DESC")
    List<Team> getTeamsRanking();

}