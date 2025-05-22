package group_05.ase.data_scraper.Service.embeddings;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.QueryFactory.nearest;
import static io.qdrant.client.VectorsFactory.vectors;

@Service
public class QdrantService  {

    private String url = "localhost";
    private int port = 6334;
    private int vectorDimension = 1536;
    private QdrantClient client;

    public QdrantService() {
        this.client = new QdrantClient(QdrantGrpcClient.newBuilder(url,port,false).build());
    }

    public void createCollection(String collectionName) {
        try {
            client.createCollectionAsync(collectionName,
                    Collections.VectorParams.newBuilder().setDistance(Collections.Distance.Dot).setSize(vectorDimension).build()).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCollection(String collectionName) {
        try {
            client.deleteCollectionAsync(collectionName).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while deleting collection: " + collectionName, e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error executing deleteCollection for: " + collectionName, e);
        }
    }

    public void upsertEntry(float[] vector, String collectionName, int id) {
        Points.PointStruct p = Points.PointStruct.newBuilder().setId(id(id)).setVectors(vectors(vector)).build();
        List<Points.PointStruct> points = List.of(p);

        try {
            client.upsertAsync(collectionName,points).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    // Todo: use later on in another service
    public ArrayList<Integer> doMatching(float[] interests, String collectionName, int resultSize) {
        try {
            List<Points.ScoredPoint> searchResult =
                    client.queryAsync(Points.QueryPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .setLimit(resultSize)
                            .setQuery(nearest(interests))
                            .build()).get();
            ArrayList<Integer> wikiIds = new ArrayList<>();

            if (searchResult != null && !searchResult.isEmpty()) {
                for (Points.ScoredPoint scoredPoint : searchResult) {
                    int qdrantId = (int) scoredPoint.getId().getNum();
                    wikiIds.add(qdrantId);
                }
            }

            return wikiIds;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
