package group_05.ase.orchestrator.dto;

import java.util.UUID;

public class UserInterestsDTO {
    private UUID userId;
    private int interestId;
    private String interestNameEn;

    public UserInterestsDTO(UUID userId, int interestId, String interestNameEn) {
        this.userId = userId;
        this.interestId = interestId;
        this.interestNameEn = interestNameEn;
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

    public String getInterestNameEn() {
        return interestNameEn;
    }

    public void setInterestNameEn(String interestNameEn) {
        this.interestNameEn = interestNameEn;
    }
}