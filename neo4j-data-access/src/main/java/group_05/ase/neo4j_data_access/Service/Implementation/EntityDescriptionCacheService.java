package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.Service.Interface.IEntityDescriptionCacheService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EntityDescriptionCacheService implements IEntityDescriptionCacheService {

    private final int MAX_CACHE_SIZE = 1000;

    private Map<String, String> urlDescriptionCache = new ConcurrentHashMap<>();

    public boolean isInCache(String url){
        return urlDescriptionCache.containsKey(url);
    }

    public String getCachedDescription(String url){
        return urlDescriptionCache.get(url);
    }

    public void addDescriptionToCache(String url, String description){

        //FIFO Principle for Caching
        if(urlDescriptionCache.size() == MAX_CACHE_SIZE){
            urlDescriptionCache.remove(urlDescriptionCache.keySet().iterator().next());
        }
        urlDescriptionCache.put(url, description);
        System.out.println("Cache-usage: " + (urlDescriptionCache.keySet().size() / MAX_CACHE_SIZE) * 100 + "%");
    }

    public String extractMainArticleText(String url) {
        //check cache
        if(isInCache(url)){
            System.out.println("Retrieved description from cache for " + url);
            return getCachedDescription(url);
        }

        StringBuilder textContent = new StringBuilder();

        try {
            Document doc = Jsoup.connect(url).get();

            Element contentDiv = doc.selectFirst("div.mw-parser-output");

            if (contentDiv != null) {
                Elements paragraphs = contentDiv.select("p");

                for (Element paragraph : paragraphs) {
                    String text = paragraph.text().trim();
                    if (!text.isEmpty()) {
                        textContent.append(text).append("\n\n");
                    }
                }
            } else {
                System.err.println("Main content div not found.");
            }

        } catch (IOException e) {
            System.err.println("Error fetching URL: " + e.getMessage());
        }

        String[] parts = textContent.toString().split("\n", 7);
        if (parts.length < 7) {
            return "";
        } else {
            String rest = parts[6];
            rest = rest.replaceAll("\n", "");

            //add to cache
            addDescriptionToCache(url, rest);
            System.out.println("Added description to cache for " + url);

            return rest;
        }
    }

}
