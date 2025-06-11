package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.QuestionEntity;
import group_05.ase.user_db.entities.QuestionResultsEntity;
import group_05.ase.user_db.entities.QuizEntity;
import group_05.ase.user_db.entities.QuizUserEntity;
import group_05.ase.user_db.repositories.QuestionRepository;
import group_05.ase.user_db.repositories.QuestionResultsRepository;
import group_05.ase.user_db.repositories.QuizRepository;
import group_05.ase.user_db.repositories.QuizUserRepository;
import group_05.ase.user_db.restData.*;
import org.springframework.data.util.Pair;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizService {

    private final QuestionRepository questionRepository;
    private final QuestionResultsRepository questionResultsRepository;
    private final QuizRepository quizRepository;
    private final QuizUserRepository quizUserRepository;
    private final TourService tourService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OPENAI_ADDRESS = "http://openai-adapter:8088/";
    private final String ORCHESTRATOR_ADDRESS = "http://orchestrator:9098/";

    private enum Category {
        LATEST_TOUR,
        SPECIFIC_TOUR,
        INTERESTS,
        FUN_FACTS,
        POPULAR,
        PARTY
    }

    public QuizService(QuestionRepository questionRepository, QuestionResultsRepository questionResultsRepository, QuizRepository quizRepository, QuizUserRepository quizUserRepository, TourService tourService) {
        this.questionRepository = questionRepository;
        this.questionResultsRepository = questionResultsRepository;
        this.quizRepository = quizRepository;
        this.quizUserRepository = quizUserRepository;
        this.tourService = tourService;
    }

    public List<QuizDTO> getQuizzesForUser(UUID user) {
        List<QuizDTO> result = new ArrayList<>();
        List<QuizUserEntity> userQuizzes = quizUserRepository.findByPlayer(user);

        for (QuizUserEntity quizUser : userQuizzes) {
            int quizId = quizUser.getQuiz();
            List<QuestionEntity> questions = questionRepository.findByQuiz(quizId);
            QuizEntity quiz = quizRepository.findById(quizId);
            QuizDTO quizDTO = mapToQuizDTO(quiz, questions);
            result.add(quizDTO);
        }

        return result;
    }

    public List<QuizResultDTO> getResultsForUser(UUID user) {
        List<QuizResultDTO> results = new ArrayList<>();
        List<QuizUserEntity> userQuizzes = quizUserRepository.findByPlayer(user);

        for (QuizUserEntity quiz : userQuizzes) {
            QuizResultDTO result = new QuizResultDTO();
            int quizId = quiz.getQuiz();
            result.setQuiz(quizId);
            List<QuestionEntity> questions = questionRepository.findByQuiz(quizId);
            List<QuizFriendResultDTO> friendResultDTOs = new ArrayList<>();

            for (QuestionEntity question : questions) {
                int questionId = question.getId();
                List<QuestionResultsEntity> questionResults = questionResultsRepository.findByQuestion(questionId);

                List<QuestionResultsDTO> questionResultsDTOs = new ArrayList<>();
                for (QuestionResultsEntity questionResultsEntity : questionResults) {
                    UUID player = questionResultsEntity.getPlayer();
                    if (user.equals(player)) {
                        questionResultsDTOs.add(mapToQuestionResultsDTO(questionResultsEntity));
                    } else {

                        QuizFriendResultDTO friendResult = null;
                        for (QuizFriendResultDTO friend : friendResultDTOs) {
                            if (player.equals(friend.getFriend())) {
                                friendResult = friend;
                                break;
                            }
                        }

                        if (friendResult == null) {
                            QuizFriendResultDTO newFriend = new QuizFriendResultDTO();
                            newFriend.setQuiz(quizId);
                            newFriend.setFriend(player);
                            newFriend.setCorrectnessPercentage(questionResultsEntity.getCorrect() ? 1f : 0f);
                            newFriend.setQuestionsAnswered(1);
                            friendResultDTOs.add(newFriend);
                        } else {
                            float percentage = friendResult.getCorrectnessPercentage();
                            int questionsAnsweredSoFar = friendResult.getQuestionsAnswered();
                            int numberOfCorrectlyAnsweredQuestionsSoFar = (int) (percentage * questionsAnsweredSoFar);
                            int newQuestionAnsweredCorrectly = questionResultsEntity.getCorrect() ? 1 : 0;
                            int newNumberOfQuestionsAnswered = friendResult.getQuestionsAnswered() + 1;
                            float newPercentage = ((float) (numberOfCorrectlyAnsweredQuestionsSoFar + newQuestionAnsweredCorrectly)) / ((float) newNumberOfQuestionsAnswered);

                            friendResult.setCorrectnessPercentage(newPercentage);
                            friendResult.setQuestionsAnswered(newNumberOfQuestionsAnswered);
                        }
                    }
                }

                result.setQuestionResults(questionResultsDTOs);
            }

            result.setFriendResults(friendResultDTOs);
            results.add(result);
        }

        return results;
    }

    public QuizDTO saveQuiz(String category, List<UUID> users) {
        //todo call build creation criteria based on users and category (tours, interests, fun facts ect.)
        UUID creator = users.getFirst();

        List<Pair<String, String>> contentData = fetchGenerationDataByCategoryForUser(creator, category);

        List<QuizResponse> responses = new ArrayList<>();
        List<String> questionData = getColumn(contentData, true);
        for (String questionPrompt : questionData) {
            responses.add(generateQuizByLLM(questionPrompt));
        }

        List<String> imageData = getColumn(contentData, false);
        enrichResponseWithImageData(responses, imageData);

        QuizDTO result = new QuizDTO();

        result.setId(100);
        result.setName("persisted Quiz");
        result.setDescription("generated");
        result.setCategory(category);
        result.setCreator(creator);
        result.setQuestions(mapQuestionResponsesToQuestionDTOList(responses));
        result.setCreatedAt(LocalDateTime.now());

        QuizEntity quizEntity = quizRepository.save(mapToQuizEntity(result));
        List<QuestionEntity> questionEntities = questionRepository.saveAll(mapToQuestionEntityList(result.getQuestions()));
        return mapToQuizDTO(quizEntity, questionEntities);

        //todo add users to quiz via quiz_user table
    }

    private QuizDTO mapToQuizDTO(QuizEntity quiz, List<QuestionEntity> questions) {
        QuizDTO result = new QuizDTO();

        result.setId(quiz.getId());
        result.setName(quiz.getName());
        result.setDescription(quiz.getDescription());
        result.setCategory(quiz.getCategory());
        result.setCreator(quiz.getCreator());
        result.setQuestions(mapToQuestionDTOList(questions));
        result.setCreatedAt(quiz.getCreatedAt());

        return result;
    }

    private List<QuestionDTO> mapToQuestionDTOList(List<QuestionEntity> questions) {
        List<QuestionDTO> result = new ArrayList<>();

        for (QuestionEntity entity : questions) {
            QuestionDTO dto = new QuestionDTO();

            dto.setId(entity.getId());
            dto.setQuiz(entity.getQuiz());
            dto.setQuestion(entity.getQuestion());
            dto.setAnswer(entity.getAnswer());
            dto.setWrongAnswerA(entity.getWrongAnswerA());
            dto.setWrongAnswerB(entity.getWrongAnswerB());
            dto.setWrongAnswerC(entity.getWrongAnswerC());
            dto.setImage(entity.getImage());
            dto.setCreatedAt(entity.getCreated_at());

            result.add(dto);
        }

        return result;
    }

    private QuestionResultsDTO mapToQuestionResultsDTO(QuestionResultsEntity entity) {
        QuestionResultsDTO dto = new QuestionResultsDTO();

        dto.setId(entity.getId());
        dto.setQuestion(entity.getQuestion());
        dto.setPlayer(entity.getPlayer());
        dto.setCorrect(entity.getCorrect());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    private QuizEntity mapToQuizEntity(QuizDTO dto) {
        QuizEntity entity = new QuizEntity();

        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCreator(dto.getCreator());
        entity.setCategory(dto.getCategory());
        entity.setCreatedAt(dto.getCreatedAt());

        return entity;
    }

    private List<QuestionEntity> mapToQuestionEntityList(List<QuestionDTO> dtoList) {
        List<QuestionEntity> result = new ArrayList<>();

        for (QuestionDTO dto : dtoList) {
            QuestionEntity entity = new QuestionEntity();

            if (dto.getId() != 0) {
                entity.setId(dto.getId());
            }

            entity.setQuiz(dto.getQuiz());
            entity.setQuestion(dto.getQuestion());
            entity.setAnswer(dto.getAnswer());
            entity.setWrongAnswerA(dto.getWrongAnswerA());
            entity.setWrongAnswerB(dto.getWrongAnswerB());
            entity.setWrongAnswerC(dto.getWrongAnswerC());
            entity.setImage(dto.getImage());
            entity.setCreated_at(dto.getCreatedAt());

            result.add(entity);
        }

        return result;
    }

    private QuizResponse generateQuizByLLM(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<>(prompt, headers);
        ResponseEntity<QuizResponse> response = restTemplate.exchange(OPENAI_ADDRESS + "/api/quiz/generate", HttpMethod.POST, entity, QuizResponse.class);
        System.out.println("RESPONSE: " + response);
        return response.getBody();
    }

    private List<Pair<String, String>> fetchGenerationDataByCategoryForUser(UUID user, String category) {
        List<Pair<String, String>> results = new ArrayList<>(); //first: content, second: image
        TourDTO tour = null;
        int tourId = -1;

        if (category.startsWith("SPECIFIC_TOUR")) {
            tourId = Integer.parseInt(category.substring(category.indexOf("=")+1, category.length()-1));
            category = "SPECIFIC_TOUR";
        }

        switch (Category.valueOf(category)) {
            case LATEST_TOUR -> {
                tour = this.tourService.findToursByUserId(String.valueOf(user)).getLast();

                List<Pair<String, String>> result = extractStringsFromTour(tour).values().stream().toList();
                results = result.stream().filter((item) -> item.getFirst().length() > 100).toList();
            }
            case SPECIFIC_TOUR -> {
                tour = this.tourService.findTourByTourId(tourId);

                List<Pair<String, String>> result = extractStringsFromTour(tour).values().stream().toList();
                results = result.stream().filter((item) -> item.getFirst().length() > 100).toList();
            }  /*
            case INTERESTS -> {
            http://localhost:9098/buildings/filtered/byUser/57cbf83e-919d-4b20-baa7-113cdf116db0?latitude=48.19994406631644&longitude=16.371089994357767&radius=1000
                // todo gather articles by interest -> need to extract long, lat and radius for that
                System.out.println("INTERESTS: not implemented yet");
            }
            case FUN_FACTS -> {
                // todo gather fun facts
                System.out.println("FUN_FACTS: not implemented yet");
            }
            case POPULAR -> {
                // todo gather articles by article weights
                System.out.println("POPULAR: not implemented yet");
            }
            case PARTY -> {
                // todo gather random entries from above from random users from the list
                System.out.println("PARTY: not implemented yet");
            } */
        }

        return results;
    }

    private Map<String, Pair<String, String>> extractStringsFromTour(TourDTO tour) {
        String stops = tour.getStops();
        String[] res = stops.split("\"viennaHistoryWikiId\":");
        Map<String, Pair<String, String>> map = new HashMap<>();

        for (int i = 1; i< res.length; i++) {
            String viennaHistoryWikiId = res[i].substring(0, res[i].indexOf(","));
            String contentTillEnd = res[i].substring(res[i].indexOf("contentGerman\":"), res[i].length()-1);
            String contentGerman = contentTillEnd.substring("contentGerman\":".length()+1, contentTillEnd.indexOf("\","));
            String image = "";
            map.put(viennaHistoryWikiId, Pair.of(contentGerman, image));
        }

        return map;
    }

    private List<QuestionDTO> mapQuestionResponsesToQuestionDTOList(List<QuizResponse> responses) {
        List<QuestionDTO> result = new ArrayList<>();

        for (QuizResponse response : responses) {
            QuestionDTO dto = new QuestionDTO();

            dto.setQuestion(response.getQuestion());
            dto.setAnswer(response.getAnswer());
            dto.setWrongAnswerA(response.getWrongAnswerA());
            dto.setWrongAnswerB(response.getWrongAnswerB());
            dto.setWrongAnswerC(response.getWrongAnswerC());
            dto.setImage(response.getImage());
            dto.setCreatedAt(LocalDateTime.now());

            result.add(dto);
        }

        return result;
    }

    private List<String> getColumn(List<Pair<String, String>> content, boolean getFirst) {
        List<String> list = new ArrayList<>();

        for (Pair<String, String> pair : content) {
            if (getFirst) {
                list.add(pair.getFirst());
            } else {
                list.add(pair.getSecond());
            }
        }

        return list;
    }

    private void enrichResponseWithImageData(List<QuizResponse> responses, List<String> images) {
        for (int i = 0; i < responses.size(); i++) {
            responses.get(i).setImage(images.get(i));
        }
    }
}