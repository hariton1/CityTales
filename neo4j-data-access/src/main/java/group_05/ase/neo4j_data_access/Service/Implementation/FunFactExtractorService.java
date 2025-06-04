package group_05.ase.neo4j_data_access.Service.Implementation;

import group_05.ase.neo4j_data_access.DTO.FunFactResult;
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

    private static final List<String> HUMOR_KEYWORDS = Arrays.asList(
            "witzig", "lustig", "kurios", "scherz", "geheimnis", "ironie", "legende", "sage",
            "Geist", "Spuk", "angeblich", "sagt man", "heißt es", "Gerücht", "Mythos", "besonders"
    );

    private double humorScore(String sentence) {
        String lower = sentence.toLowerCase();
        return HUMOR_KEYWORDS.stream()
                .filter(lower::contains)
                .count() * 1.0; // 1 Punkt je gefundenem Keyword
    }

    public String extractFunFactHumorAware(String text) {
        List<String> sentences = splitIntoSentences(text);
        if (sentences.isEmpty()) return "";

        String bestSentence = "";
        double bestScore = -1;

        for (String sentence : sentences) {
            // Humor zählt stärker als alles andere!
            double score = humorScore(sentence) * 3.0
                    + baseHeuristicScore(sentence); // kann optional kombiniert werden
            if (score > bestScore) {
                bestScore = score;
                bestSentence = sentence;
            }
        }
        // Fallback: Kein humorvoller Satz gefunden? Nimm TF-IDF
        if (bestScore < 0.01) {
            return extractFunFactTFIDF(text);
        }
        return bestSentence;
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

    public FunFactResult extractFunFactHybridWithReason(String text) {
        List<String> sentences = splitIntoSentences(text);
        if (sentences.isEmpty()) return new FunFactResult("", 0, "Kein Satz gefunden.");

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


}
