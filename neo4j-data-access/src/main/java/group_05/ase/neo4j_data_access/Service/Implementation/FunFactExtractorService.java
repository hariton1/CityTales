package group_05.ase.neo4j_data_access.Service.Implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FunFactExtractorService {
    public String extractFunFact(String storyText) {
        List<String> sentences = splitIntoSentences(storyText);
        String bestSentence = "";
        double bestScore = -1;
        for (String sentence : sentences) {
            double score = 0;
            // Heuristic: numbers
            if (sentence.matches(".*\\d+.*")) score += 1.0;
            // Heuristic: superlatives and interesting words
            if (sentence.matches(".*(oldest|first|most|best|largest|famous|haunted|legend|loved|helped|ghost).*")) score += 0.8;
            // Heuristic: names/entities (simulate: capitalized word in middle of sentence)
            if (sentence.matches(".*[A-Z][a-z]+.*")) score += 0.5;
            // Penalize short sentences
            if (sentence.length() < 30) score -= 0.5;
            // Bonus: longer sentences might have more context
            score += sentence.length() / 100.0;
            if (score > bestScore) {
                bestScore = score;
                bestSentence = sentence;
            }
        }
        return bestSentence;
    }

    public List<String> splitIntoSentences(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return Arrays.asList(text.split("(?<=[.!?])\\s+"));
    }
}
