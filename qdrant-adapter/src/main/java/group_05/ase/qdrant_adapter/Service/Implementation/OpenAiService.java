package group_05.ase.qdrant_adapter.Service.Implementation;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiService {

    @Autowired
    private EmbeddingModel embeddingModel;

    public float[] getEmbedding(String content) {

        List<String> contentList = new ArrayList<>();
        contentList.add(content);

        EmbeddingResponse r = embeddingModel.embedForResponse(contentList);
        return r.getResult().getOutput();
    }
}
