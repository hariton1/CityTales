package group_05.ase.data_scraper.Service.embeddings;

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
    public void doStuff() {
        List<String> listL = new ArrayList<>();
        listL.add("literature");

        List<String> listM = new ArrayList<>();
        listL.add("music");

        List<String> listLP= new ArrayList<>();
        listL.add("Mozart");

        List<String> listMP= new ArrayList<>();
        listL.add("Faust");

        EmbeddingResponse r = embeddingModel.embedForResponse(listL);
        System.out.println(r.getResult().getOutput().length);
    }

    public float[] getEmbedding(String content) {
        List<String> contentList = new ArrayList<>();
        contentList.add(content);

        EmbeddingResponse r = embeddingModel.embedForResponse(contentList);
        return r.getResult().getOutput();
    }
}
