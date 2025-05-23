package group_05.ase.data_scraper.Service.embeddings;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class OpenAiService {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private ChatModel chatModel;

    public float[] getEmbedding(String content) {
        List<String> contentList = new ArrayList<>();
        contentList.add(content);

        EmbeddingResponse r = embeddingModel.embedForResponse(contentList);
        return r.getResult().getOutput();
    }

    public String getGermanToEnglishTranslation(String germanText) {
        String prompt = "Translate this German text to English and give me ONLY the result: \"" + germanText + "\"";
        ChatResponse r = chatModel.call(new Prompt(prompt));
        return r.getResult().getOutput().getContent();
    }
}
