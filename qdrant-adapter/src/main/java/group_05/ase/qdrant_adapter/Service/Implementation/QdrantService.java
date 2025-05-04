package group_05.ase.qdrant_adapter.Service.Implementation;

import group_05.ase.qdrant_adapter.Service.Interface.IVectorDBService;

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
public class QdrantService implements IVectorDBService {

    private String url = "localhost";
    private int port = 6334;
    private int vectorDimension = 1536;
    private QdrantClient client;

    public QdrantService() {
        this.client = new QdrantClient(QdrantGrpcClient.newBuilder(url,port,false).build());
    }

    @Override
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

    @Override
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

    @Override
    public void upsertEntry(List<Float> vector, String collectionName, String wikiDataId) {
        int id = mapWikiDataIdToQdrantId(wikiDataId);
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

    @Override
    public ArrayList<String> doMatching(List<Float> interests, String collectionName, int resultSize) {
        try {
            List<Points.ScoredPoint> searchResult =
                    client.queryAsync(Points.QueryPoints.newBuilder()
                            .setCollectionName(collectionName)
                            .setLimit(resultSize)
                            .setQuery(nearest(interests))
                            .build()).get();
            ArrayList<String> wikiDataIds = new ArrayList<>();

            if (searchResult != null && !searchResult.isEmpty()) {
                for (Points.ScoredPoint scoredPoint : searchResult) {
                    long qdrantId = scoredPoint.getId().getNum();
                    String wikiDataId = mapQdrantIdToWikiDataId((int) qdrantId);
                    wikiDataIds.add(wikiDataId);
                }
            }

            return wikiDataIds;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private int mapWikiDataIdToQdrantId(String wikiDataId) {
        if (wikiDataId == null || !wikiDataId.startsWith("Q")) {
            throw new IllegalArgumentException("Invalid Wikidata ID: " + wikiDataId);
        }
        return Integer.parseInt(wikiDataId.substring(1));
    }

    private String mapQdrantIdToWikiDataId(int qdrantId) {
        if (qdrantId < 0) {
            throw new IllegalArgumentException("Invalid Qdrant ID: " + qdrantId);
        }
        return "Q" + qdrantId;
    }
}
