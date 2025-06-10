package group_05.ase.orchestrator.dto;

import java.util.UUID;

public class UserInterestsDTO {
    private UUID userId;
    private int interestId;
    private String interestNameDe;

    public UserInterestsDTO(UUID userId, int interestId, String interestNameDe) {
        this.userId = userId;
        this.interestId = interestId;
        this.interestNameDe = interestNameDe;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public int getInterestId() {
        return interestId;
    }

    public void setInterestId(int interestId) {
        this.interestId = interestId;
    }

    public String getInterestNameDe() {
        return interestNameDe;
    }

    public void setInterestNameDe(String interestNameDe) {
        this.interestNameDe = interestNameDe;
    }
}