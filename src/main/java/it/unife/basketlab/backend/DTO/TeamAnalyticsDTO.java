package it.unife.basketlab.backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeamAnalyticsDTO {

    private Double tempo_corsa;
    private Double percentuale_tiri;

    public TeamAnalyticsDTO() {
    }

    public TeamAnalyticsDTO(Double tempo_corsa, Double percentuale_tiri) {
        this.tempo_corsa = tempo_corsa;
        this.percentuale_tiri = percentuale_tiri;
    }
    
}
