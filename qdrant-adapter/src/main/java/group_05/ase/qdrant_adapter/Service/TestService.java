package group_05.ase.qdrant_adapter.Service;

import com.cohere.api.Cohere;
import com.cohere.api.resources.v2.requests.V2EmbedRequest;
import com.cohere.api.types.*;
import io.github.cdimascio.dotenv.Dotenv;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.VectorsFactory.vectors;


public class TestService {
    private Dotenv dotenv = Dotenv.load();
    String apiKey = dotenv.get("QDRANT_API_KEY");
    private QdrantClient client = new QdrantClient(QdrantGrpcClient.newBuilder("localhost",6334,false).build());
    private int vectorDimension = 1536; // This is the Cohere-Default

    public TestService() {
    }
    public void createHistoricPersonsList() {
        String personsList = "historicPersons";

        try {
            client.createCollectionAsync(personsList,
                    VectorParams.newBuilder().setDistance(Distance.Dot).setSize(vectorDimension).build()).get();
        } catch (Exception e) {

        }
    }

    public void upsertHistoricPersons(String text, int wikiDataId) {
        Cohere cohere = Cohere.builder().token(apiKey).clientName("snippet").build();

        List<EmbeddingType> embeddingTypes = Arrays.asList(EmbeddingType.FLOAT);

        List<String> texts = new ArrayList<>();
        texts.add(text);

        EmbedByTypeResponse response =
                cohere.v2().embed(V2EmbedRequest.builder().model("embed-v4.0").inputType(EmbedInputType.CLASSIFICATION).embeddingTypes(embeddingTypes).texts(texts).build());

        EmbedByTypeResponseEmbeddings embedding = response.getEmbeddings();

        try {
            upsertEmbedding(embedding,wikiDataId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public EmbedByTypeResponseEmbeddings getInterestEmbedding(String interest) {
        List<String> interestList = new ArrayList<>();
        interestList.add(interest);

        List<EmbeddingType> embeddingTypes = Arrays.asList(EmbeddingType.FLOAT);
        Cohere cohere = Cohere.builder().token(apiKey).clientName("snippet").build();

        EmbedByTypeResponse response =
                cohere.v2().embed(V2EmbedRequest.builder().model("embed-v4.0").inputType(EmbedInputType.CLASSIFICATION).embeddingTypes(embeddingTypes).texts(interestList).build());

        System.out.println(response.getEmbeddings().getFloat());
        return response.getEmbeddings();
    }

    public void upsertEmbedding(EmbedByTypeResponseEmbeddings embedding,int wikiDataId) throws ExecutionException, InterruptedException {
        Optional<List<List<Double>>> optionalVector = embedding.getFloat();
        List<Float> floatList = new ArrayList<>();

        if (optionalVector.isPresent()) {
            for (List<Double> innerList : optionalVector.get()) {
                for (Double d : innerList) {
                    floatList.add(d.floatValue());
                }
            }
        }
        Points.PointStruct p = Points.PointStruct.newBuilder().setId(id(wikiDataId)).setVectors(vectors(floatList)).build();

        List<Points.PointStruct> points =
                List.of(p);

        Points.UpdateResult updateResult = client.upsertAsync("historicPersons",points).get();
        System.out.println(updateResult);
    }

    public void matching(EmbedByTypeResponseEmbeddings interestsEmbedding) {
        Optional<List<List<Double>>> optionalVector = interestsEmbedding.getFloat();
        List<Float> floatList = new ArrayList<>();

        if (optionalVector.isPresent()) {
            for (List<Double> innerList : optionalVector.get()) {
                for (Double d : innerList) {
                    floatList.add(d.floatValue());
                }
            }
        }

        try {
            List<Points.ScoredPoint> searchResult =
                    client.queryAsync(Points.QueryPoints.newBuilder()
                            .setCollectionName("historicPersons")
                            .setLimit(3)
                            .setQuery(nearest(floatList))
                            .build()).get();

            if (searchResult != null && !searchResult.isEmpty()) {
                System.out.println("Search results: ");
                for (Points.ScoredPoint scoredPoint : searchResult) {
                    System.out.println("ID: " + scoredPoint.getId() + ", Score: " + scoredPoint.getScore());
                }
            } else {
                System.out.println("No similar vectors found.");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
