package group_05.ase.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleWeightDTO {

    @JsonProperty(required = true, value = "article_weight_id")
    private int articleWeightId;

    @JsonProperty(required = true, value = "article_id")
    private int articleId;

    @JsonProperty(required = true, value = "weight")
    private float weight;

    public ArticleWeightDTO(int articleWeightId, int articleId, float weight) {
        this.articleWeightId = articleWeightId;
        this.articleId = articleId;
        this.weight = weight;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getArticleWeightId() {
        return articleWeightId;
    }

    public void setArticleWeightId(int articleWeightId) {
        this.articleWeightId = articleWeightId;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
