package group_05.ase.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInterestsDTO {
    private UUID userId;
    private int interestId;
    private String interestNameEn;
}