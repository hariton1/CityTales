package group05.ase.openai.adapter.dto;

import lombok.Data;

@Data
public class EnrichmentRequest {
    private String tone;
    private String content;
}

