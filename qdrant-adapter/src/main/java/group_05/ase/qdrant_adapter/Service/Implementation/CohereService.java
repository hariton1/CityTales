package group_05.ase.qdrant_adapter.Service.Implementation;

import com.cohere.api.Cohere;
import com.cohere.api.resources.v2.requests.V2EmbedRequest;
import com.cohere.api.types.EmbedByTypeResponse;
import com.cohere.api.types.EmbedByTypeResponseEmbeddings;
import com.cohere.api.types.EmbedInputType;
import com.cohere.api.types.EmbeddingType;
import group_05.ase.qdrant_adapter.Service.Interface.IEmbeddingsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CohereService implements IEmbeddingsService {
    String apiKey = "9FVFm1TSpCbgSVIvtKS0SRXZ61LWnEBexUYzzJwt";
    private List<EmbeddingType> embeddingTypes = Arrays.asList(EmbeddingType.FLOAT);;


    public CohereService() {
    }

    @Override
    public List<Float> getInterestsEmbedding(List<String> interests) {
        Cohere cohere = Cohere.builder().token(apiKey).clientName("snippet").build();

        String concatenatedInterests = String.join(", ", interests);
        List<String> input = List.of(concatenatedInterests);

        EmbedByTypeResponse response =
                cohere.v2().embed(V2EmbedRequest.builder().model("embed-v4.0").inputType(EmbedInputType.CLASSIFICATION).embeddingTypes(embeddingTypes).texts(input).build());

        return mapEmbedByTypeResponseEmbeddingsToFloatList(response.getEmbeddings());
    }

    @Override
    public List<Float> getArticleEmbedding(String content) {
        List<String> contents = new ArrayList<>();
        contents.add(content);

        Cohere cohere = Cohere.builder().token(apiKey).clientName("snippet").build();
        EmbedByTypeResponse response =
                cohere.v2().embed(V2EmbedRequest.builder().model("embed-v4.0").inputType(EmbedInputType.CLASSIFICATION).embeddingTypes(embeddingTypes).texts(contents).build());

        return mapEmbedByTypeResponseEmbeddingsToFloatList(response.getEmbeddings());
    }

    private List<Float> mapEmbedByTypeResponseEmbeddingsToFloatList(EmbedByTypeResponseEmbeddings embedding) {
        Optional<List<List<Double>>> optionalVector = embedding.getFloat();
        List<Float> floatList = new ArrayList<>();

        if (optionalVector.isPresent()) {
            for (List<Double> innerList : optionalVector.get()) {
                for (Double d : innerList) {
                    floatList.add(d.floatValue());
                }
            }
        }

        return floatList;
    }
}
