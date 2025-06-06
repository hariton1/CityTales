package group_05.ase.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInterestWithWeightDTO {
    private int interestId;
    private String interestNameEn;
    private float interestWeight;
}