package it.unife.basketlab.backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsDTO {

    private Double tempo_corsa;
    private Double percentuale_tiri;

    public AnalyticsDTO() {}

    public AnalyticsDTO(Double tempo_corsa, Double percentuale_tiri) {
        this.tempo_corsa= tempo_corsa;
        this.percentuale_tiri= percentuale_tiri;
    }
    
}
