package group_05.ase.neo4j_data_access.Service.Implementation;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FunFactExtractorService {

    public String extractFunFactTFIDF(String text) {
        List<String> sentences = splitIntoSentences(text);
        if (sentences.isEmpty()) return "";

        // 1. Tokenize alles, baue ein globales Vokabular
        List<List<String>> tokenized = sentences.stream()
                .map(this::tokenize)
                .collect(Collectors.toList());

        Set<String> vocabulary = tokenized.stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // 2. Berechne für jeden Satz den TF-IDF Score
        double bestScore = -1;
        String bestSentence = "";

        for (int i = 0; i < sentences.size(); i++) {
            List<String> tokens = tokenized.get(i);
            double score = 0;
            for (String token : tokens) {
                double tf = (double) Collections.frequency(tokens, token) / tokens.size();
                double idf = Math.log((double) sentences.size() /
                        (1 + countSentencesContaining(token, tokenized)));
                score += tf * idf;
            }
            if (score > bestScore) {
                bestScore = score;
                bestSentence = sentences.get(i);
            }
        }
        return bestSentence;
    }

    private int countSentencesContaining(String token, List<List<String>> tokenized) {
        int count = 0;
        for (List<String> sent : tokenized) {
            if (sent.contains(token)) count++;
        }
        return count;
    }

    private List<String> tokenize(String sentence) {
        return Arrays.stream(sentence.toLowerCase().split("[^a-z0-9]+"))
                .filter(s -> s.length() > 2)
                .collect(Collectors.toList());
    }

    public String extractFunFact(String text) {
        List<String> sentences = splitIntoSentences(text);
        String bestSentence = "";
        double bestScore = -1;
        for (String sentence : sentences) {
            double score = 0;
            // Heuristic: numbers
            if (sentence.matches(".*\\d+.*")) score += 1.0;
            // Heuristic: superlatives & interesting words
            if (sentence.matches(".*(oldest|first|most|best|largest|famous|haunted|legend|loved|helped|ghost).*")) score += 0.8;
            // Named entities (very basic): Capitalized word not at start
            if (sentence.matches(".*[A-Z][a-z]+.*")) score += 0.5;
            if (sentence.length() < 30) score -= 0.5;
            score += sentence.length() / 100.0;
            if (score > bestScore) {
                bestScore = score;
                bestSentence = sentence;
            }
        }
        return bestSentence;
    }

    private List<String> splitIntoSentences(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return Arrays.asList(text.split("(?<=[.!?])\\s+"));
    }
}
