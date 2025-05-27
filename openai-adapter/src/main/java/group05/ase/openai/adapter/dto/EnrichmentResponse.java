package group05.ase.openai.adapter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrichmentResponse {
    private String summary;
    private String enrichedContent;
    private String tone;
}
