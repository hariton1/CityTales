package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.DTO.FunFactResult;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FunFactExtractorService {

    private static final List<String> HUMOR_KEYWORDS = Arrays.asList(
            "witzig", "lustig", "kurios", "scherz", "geheimnis", "ironie", "legende", "sage",
            "geist", "spuk", "angeblich", "sagt man", "heißt es", "gerücht", "mythos", "besonders"
    );

    private final RestTemplate restTemplate;
    private final String pythonUrl = "http://funfact-ml-service:5005/funfact";


    public FunFactExtractorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public FunFactExtractorService() {
        this.restTemplate = new RestTemplate();
    }

    public FunFactResult extractFunFactHybridWithReason(String text) {
        List<String> sentences = splitIntoSentences(text);
        if (sentences.isEmpty())
            return new FunFactResult("", 0, "Kein Satz gefunden.");

        String bestSentence = "";
        double bestScore = -1;
        String bestReason = "";

        for (String sentence : sentences) {
            double humor = humorScore(sentence);
            double tfidf = tfidfScore(sentence, sentences);
            double heur = baseHeuristicScore(sentence);
            double hybridScore = humor * 3.0 + heur + tfidf;

            StringBuilder reason = new StringBuilder();
            if (humor > 0) reason.append("Humor erkannt, ");
            if (heur > 0.9) reason.append("Heuristik: Zahl/Superlativ, ");
            if (tfidf > 0.7) reason.append("Seltenheit im Text, ");

            if (hybridScore > bestScore) {
                bestScore = hybridScore;
                bestSentence = sentence;
                bestReason = reason.length() > 0 ? reason.toString() : "Höchster Hybrid-Score.";
            }
        }
        return new FunFactResult(bestSentence, bestScore, bestReason);
    }

    private double humorScore(String sentence) {
        String lower = sentence.toLowerCase();
        return HUMOR_KEYWORDS.stream().filter(lower::contains).count() * 1.0;
    }

    private double baseHeuristicScore(String sentence) {
        double score = 0;
        if (sentence.matches(".*\\d+.*")) score += 1.0;
        if (sentence.matches(".*(oldest|first|most|best|largest|famous|haunted|legend|loved|helped|ghost|witzig|Legende|Geist|Jahrhundert).*")) score += 0.8;
        if (sentence.matches(".*[A-Z][a-z]+.*")) score += 0.5;
        if (sentence.length() < 30) score -= 0.5;
        score += sentence.length() / 100.0;
        return score;
    }

    private double tfidfScore(String sentence, List<String> allSentences) {
        List<String> sentenceTokens = tokenize(sentence);
        List<List<String>> tokenized = allSentences.stream().map(this::tokenize).collect(Collectors.toList());
        double score = 0;
        for (String token : sentenceTokens) {
            double tf = (double) Collections.frequency(sentenceTokens, token) / sentenceTokens.size();
            double idf = Math.log((double) allSentences.size() / (1 + countSentencesContaining(token, tokenized)));
            score += tf * idf;
        }
        return score;
    }

    private int countSentencesContaining(String token, List<List<String>> tokenized) {
        int count = 0;
        for (List<String> sent : tokenized) {
            if (sent.contains(token)) count++;
        }
        return count;
    }

    private List<String> tokenize(String sentence) {
        return Arrays.stream(sentence.toLowerCase().split("[^a-z0-9äöüß]+"))
                .filter(s -> s.length() > 2)
                .collect(Collectors.toList());
    }

    private List<String> splitIntoSentences(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return Arrays.asList(text.split("(?<=[.!?])\\s+"));
    }

    public FunFactResult extractFunFactWithML(String text) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, requestEntity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String funFact = response.getBody().get("funfact").toString();
            double score = Double.parseDouble(response.getBody().get("score").toString());
            return new FunFactResult(funFact, score, "ML-Extraktion");
        } else {
            throw new RuntimeException("Python ML service returned error: " + response.getStatusCode());
        }
    }

}
