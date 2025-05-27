package group05.ase.openai.adapter.dto;


import lombok.Data;
import java.util.List;

@Data
public class OpenAIResponseDTO {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
        private int index;
        private String finish_reason;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
