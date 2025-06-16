package group05.ase.openai.adapter.Service;

import group05.ase.openai.adapter.Controller.EnrichmentController;
import group05.ase.openai.adapter.dto.*;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentController.class);

    // OpenAI API Key
    @Value("${openai.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.openai.com/v1")
            .build();

    private static final Map<String, String> TONE_PROMPTS = Map.ofEntries(
            Map.entry("poetic", """
                    You are a poet describing historical landmarks with vivid imagery, emotional language, and elegant flow.
                    Use metaphors, personification, and expressive tone — but do not invent facts or ask questions.
                    Base everything strictly on the provided content.
                
                    Format the output as clean HTML:
                    - Use <p> for flowing, expressive paragraphs
                    - Use <em> to highlight symbolic or emotional terms
                    - Use <ul> and <li> to poetically list elements (e.g. architectural features, artistic contributions)
                    - Use <br> sparingly for rhythm or dramatic pause
                    - Do not use heading tags like <h3>
                
                    Think of the structure as a lyrical reflection: part narrative, part meditation. The list, if used, should feel like poetic emphasis — not like a technical summary.
                
                    Your words should breathe life into the place — as if the stone itself remembers.
    """),
            Map.entry("tour", """
                    You are a cheerful and engaging tour guide presenting this historical site to casual visitors.
                    Use a friendly and informative tone — as if you're speaking to a walking group curious about history.
                
                    Do not include the site name in your response.
                    Do not ask rhetorical questions or invent facts — stay strictly within the input content.
                
                    Format the output as clean HTML:
                    - Use <p> for welcoming, digestible descriptions
                    - Use <ul> and <li> to list key facts and highlights (e.g., dates, events, features)
                    - Use <strong> to emphasize important points
                    - Keep it concise, friendly, and easy to scan
                
                    Begin with a friendly overview in a <p>, followed by a <ul> of highlights if appropriate.
                    """),
            Map.entry("funny", """
                    You are a witty storyteller narrating historical facts in a light, entertaining way.
                    Add playful commentary, light sarcasm, or humorous metaphors — but do not invent facts.
                    Use clever phrasing to keep it engaging, and always stay grounded in the original content.
                
                    Format the output in clean, structured HTML:
                    - Use <p> for main text and humorous transitions
                    - Use <strong> for punchlines or emphasis
                    - Use <ul> and <li> for fun facts, quirky lists, or standout details
                    - Use <br> for comedic timing if needed
                    - Do not use heading tags like <h1> to <h6>. Instead, start paragraphs with playful phrases like "Here's the fun part:" or "Some juicy historical bits:"
                
                    Begin with an engaging intro paragraph. Then include a <ul> list of funny-but-true facts if relevant. Wrap up with a short remark or commentary in <p>.
                
                    Make it feel like a museum tour where the guide had strong coffee and no filter — fun, clever, and fact-based.
    """),
            Map.entry("academic", """
                    You are a historian writing a detailed, objective, and factual explanation of a historical site or figure for an academic audience.
                    Do not include a title or heading in your response.
                    Do not ask questions or speculate beyond the given content.
                    Present the information using structured HTML:
                    - Use <ul> with <li> elements for discrete facts (e.g., dates, roles, features)
                    - Use <p> for brief academic-style commentary or contextualization
                    - Use <strong> to emphasize dates, names, or significant terms
                    - Keep the tone formal, factual, and precise
                    Begin with a short <p> summarizing the site's importance, followed by <ul> with factual details.
                    Do not include rhetorical devices, opinions, or creative interpretations.
    """),
            Map.entry("dramatic", """
                    You are a dramatic narrator bringing a historical site to life with grandeur and intensity.
                    Use emotionally charged, suspenseful, and vivid language — as if narrating a historical documentary.
                    However, do not invent facts or ask rhetorical questions. Stay strictly within the given content.
                
                    Do not include a title or heading in your response.
                
                    Format the response using clean semantic HTML:
                    - Use <p> for rich, theatrical paragraphs
                    - Use <ul> and <li> to list pivotal facts like key dates, events, or figures — as dramatic reveals
                    - Use <strong> to emphasize powerful words, names, or transitions
                    - Use <br> for dramatic pauses if needed
                
                    Begin with a compelling narrative paragraph, followed by a factual list (<ul>) if appropriate.
                    Your tone should evoke awe, intensity, and reverence while staying truthful.
    """),
            Map.entry("child-friendly", """
                    You are an educator explaining this historical site to children aged 8 to 12 in a fun, simple, and friendly way.
                    Use short sentences, clear words, and a curious tone — like you're telling an exciting true story to a school group.
                
                    Do not include a title or heading in your response.
                    Do not ask questions or invent facts. Use only the information provided.
                
                    Format the output using clean HTML:
                    - Use <p> for short, easy-to-read explanations
                    - Use <ul> and <li> for fun or interesting facts
                    - Use <strong> to highlight names or dates
                    - Use <br> sparingly for pacing or excitement
                
                    Start with a short <p> that introduces the site in a fun way, then use <ul> for cool or important facts. Keep the tone light, clear, and exciting.
    """)
    );

    public SummaryResponse generateSummary(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new SummaryResponse("<p><em>No content provided for summary.</em></p>");
        }

        String summaryPrompt = """
                You are a historian extracting and summarizing factual information from the input content.
                
                Your output must consist of:
                1. A short summary (2–3 lines), written in formal and objective tone, without opinions or speculation.
                2. A clean HTML list of the most important key facts explicitly mentioned in the input (3–7 items).
                Formatting:
                - Wrap the summary in a single <p> tag.
                - Follow it immediately with a <ul> and <li> tags for the facts.
                - Each <li> should be 1–2 short sentences.
                - Only include facts directly present in the input. Do not speculate, interpret, or invent.
                
                Do not include any titles, headings, or extra commentary.
                Do not invent or speculate. Only restate what's provided.
                """ ;

        String summary = callOpenAI(enforceEnglish(summaryPrompt), content, "academic");

        return new SummaryResponse(summary);
    }

    public EnrichmentResponse generateEnrichedContent(String tone, String content) {
        if (content == null || content.trim().isEmpty()) {
            return new EnrichmentResponse("<p><em>No content available for enrichment.</em></p>", tone);
        }

        String toneKey = tone.toLowerCase();
        String tonePrompt = TONE_PROMPTS.getOrDefault(toneKey, TONE_PROMPTS.get("academic"));
        String enrichedContent = callOpenAI(enforceEnglish(tonePrompt), content, toneKey);

        return new EnrichmentResponse(enrichedContent, tone);
    }

    public QuizQuestionResponse generateQuiz(String quizGenerationContentPrompt) {
        String systemPrompt = """
                You are a quiz question generator. Based on the provided input information, generate exactly one concise multiple-choice question.
                Each output must always be in german and returned as a single line string in the following format:
                "Question;CorrectAnswer;WrongAnswerA;WrongAnswerB;WrongAnswerC;empty"
                Here is an example:
                "What is the capital of France?;Paris;London;Berlin;Rome;empty"
                Important requirements:
                Separate each field strictly with a semicolon (;) — no extra spaces.
                The first field is the quiz question.
                The second field is the correct answer to the question.
                The next three fields are incorrect but plausible answers.
                The final field must always be the string "empty" (without quotes).
                Do not include any other text, explanation, or formatting — return only the semicolon-separated string exactly as specified.
                """;
        String generatedQuiz = callOpenAI(systemPrompt, quizGenerationContentPrompt, "quiz");

        String[] generatedContent = generatedQuiz.split(";");
        String[] quizContent = new String[6];
        for (int i = 0; i < quizContent.length; i++) {
            quizContent[i] = generatedQuiz.length() - 1 >= i ? generatedContent[i] : "";
        }

        return new QuizQuestionResponse(quizContent[0], quizContent[1], quizContent[2], quizContent[3], quizContent[4], quizContent[5]);
    }

    public QuizAdditionalResponse generateAdditionalQuizData(String quizAdditionalQuizDataPrompt) {
        String systemPrompt = """
                Your are a fun and charismatic game show host that announces a new quiz.
                You are given a series of questions and your job is to generate a name and
                a description for this quiz.
                Name and description should both somehow reference the content of the questions
                while also being fun and engaging.
                The name must be exactly between 1 and 3 words long.
                The description must fit within 15 words.
                Each output must always be in german and returned as a single line string in the following format:
                "name;description"
                Important requirements:
                Separate each field strictly with a semicolon (;) — no extra spaces.
                The first field is the quiz name.
                The second field is the description of the quiz.
                Do not include any other text, explanation, or formatting — return only the semicolon-separated string exactly as specified.
                """;
        String generatedQuiz = callOpenAI(systemPrompt, quizAdditionalQuizDataPrompt, "funny");

        String[] generatedContent = generatedQuiz.split(";");
        String[] quizAdditionalContent = new String[2];
        for (int i = 0; i < quizAdditionalContent.length; i++) {
            quizAdditionalContent[i] = generatedQuiz.length() - 1 >= i ? generatedContent[i] : "";
        }

        return new QuizAdditionalResponse(quizAdditionalContent[0], quizAdditionalContent[1]);
    }

    protected String enforceEnglish(String prompt) {
        return prompt.endsWith(".") ? prompt + " Always respond in English." : prompt + ". Always respond in English.";
    }

    protected String callOpenAI(String systemPrompt, String content, String toneKey) {
        double temperature = switch (toneKey) {
            case "academic", "quiz" -> 0.2;
            case "tour", "child-friendly" -> 0.5;
            case "dramatic", "poetic", "funny" -> 0.7;
            default -> 0.5;
        };

        Map<String, Object> body = Map.of(
                "model", "gpt-4.1-nano",
                "temperature", temperature,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", content)
                )
        );

        OpenAIResponseDTO response = webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OpenAIResponseDTO.class)
                .block();

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            logger.warn("OpenAI returned no choices.");
            return "<p><em>No response from language model.</em></p>";
        }

        logger.info("SERVICE: OpenAI response: {}", response);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
