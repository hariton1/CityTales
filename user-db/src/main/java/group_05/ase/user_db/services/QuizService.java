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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final HttpHeaders headers = new HttpHeaders();

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    public QuizService(QuestionRepository questionRepository, QuestionResultsRepository questionResultsRepository, QuizRepository quizRepository, QuizUserRepository quizUserRepository, TourService tourService) {
        this.questionRepository = questionRepository;
        this.questionResultsRepository = questionResultsRepository;
        this.quizRepository = quizRepository;
        this.quizUserRepository = quizUserRepository;
        this.tourService = tourService;
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

    }

    public List<QuizDTO> getQuizzesForUser(UUID user) {
        List<QuizDTO> result = new ArrayList<>();
        List<QuizUserEntity> userQuizList = quizUserRepository.findByPlayer(user);

        Set<String> seen = new HashSet<>();
        List<QuizUserEntity> userQuizzes = new ArrayList<>();

        for (QuizUserEntity entity : userQuizList) {
            String key = entity.getQuiz() + ":" + entity.getPlayer().toString();

            if (!seen.contains(key)) {
                seen.add(key);
                userQuizzes.add(entity);
            }
        }

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
            QuizResultDTO result = getResultsForUserForQuiz(user, quiz.getQuiz());
            results.add(result);
        }

        return results;
    }

    public QuizResultDTO saveQuestionResults(QuizResultDTO dto) {

        List<QuestionResultsEntity> entities = mapToQuestionResultsEntity(dto);
        for (QuestionResultsEntity entity : entities) {
            questionResultsRepository.save(entity);
        }
        return getResultsForUserForQuiz(dto.getQuestionResults().getFirst().getPlayer(), dto.getQuiz());
    }

    public QuizDTO saveQuiz(String category, List<UUID> users) {
        UUID creator = users.getFirst();

        List<Pair<String, String>> contentData = fetchGenerationDataByCategoryForUser(category);

        List<QuizQuestionResponse> responses = new ArrayList<>();
        List<String> questionData = getColumn(contentData, true);
        for (String questionPrompt : questionData) {
            responses.add(generateQuizByLLM(questionPrompt));
        }

        List<String> imageData = getColumn(contentData, false);
        enrichResponseWithImageData(responses, imageData);

        QuizAdditionalResponse quizAdditionalResponse = generateQuizAdditionalByLLM(responses);

        QuizDTO result = new QuizDTO();
        result.setName(quizAdditionalResponse.getName());
        result.setDescription(quizAdditionalResponse.getDescription());
        result.setCategory(category);
        result.setCreator(creator);
        result.setCreatedAt(LocalDateTime.now());

        QuizEntity quizEntity = quizRepository.save(mapToQuizEntity(result));

        result = mapToQuizDTOWithEmptyQuestions(quizEntity);
        result.setQuestions(mapQuestionResponsesToQuestionDTOList(responses, result.getId()));

        List<QuestionEntity> questionEntities = questionRepository.saveAll(mapToQuestionEntityList(result.getQuestions()));

        addUsersToQuiz(users, result.getId());

        return mapToQuizDTO(quizEntity, questionEntities);
    }

    public List<QuizUserDTO> saveQuizForUsers(List<QuizUserDTO> dtoList) {
        List<QuizUserDTO> list = new ArrayList<>();

        for (QuizUserDTO dto : dtoList) {
            QuizUserEntity entity = new QuizUserEntity();

            entity.setQuiz(dto.getQuiz());
            entity.setPlayer(dto.getPlayer());
            entity.setCreatedAt(dto.getCreatedAt());

            List<QuizUserEntity> persistedEntities = quizUserRepository.findByPlayer(dto.getPlayer());
            boolean found = false;
            for (QuizUserEntity persistedEntity : persistedEntities) {
                if (dto.getQuiz() == (persistedEntity.getQuiz())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                QuizUserEntity result = quizUserRepository.save(entity);

                QuizUserDTO persistedDTO = new QuizUserDTO();

                persistedDTO.setId(result.getId());
                persistedDTO.setQuiz(result.getQuiz());
                persistedDTO.setPlayer(result.getPlayer());
                persistedDTO.setCreatedAt(result.getCreatedAt());

                list.add(persistedDTO);
            }
        }

        return list;
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

    private QuizDTO mapToQuizDTOWithEmptyQuestions(QuizEntity quiz) {
        QuizDTO result = new QuizDTO();

        result.setId(quiz.getId());
        result.setName(quiz.getName());
        result.setDescription(quiz.getDescription());
        result.setCategory(quiz.getCategory());
        result.setCreator(quiz.getCreator());
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
            dto.setWrongAnswerA(entity.getWrong_answer_a());
            dto.setWrongAnswerB(entity.getWrong_answer_b());
            dto.setWrongAnswerC(entity.getWrong_answer_c());
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
            entity.setWrong_answer_a(dto.getWrongAnswerA());
            entity.setWrong_answer_b(dto.getWrongAnswerB());
            entity.setWrong_answer_c(dto.getWrongAnswerC());
            entity.setImage(dto.getImage());
            entity.setCreated_at(dto.getCreatedAt());

            result.add(entity);
        }

        return result;
    }

    private QuizQuestionResponse generateQuizByLLM(String prompt) {
        HttpEntity<String> entity = new HttpEntity<>(prompt, headers);
        ResponseEntity<QuizQuestionResponse> response = restTemplate.exchange(OPENAI_ADDRESS + "/api/quiz/generate", HttpMethod.POST, entity, QuizQuestionResponse.class);
        return response.getBody();
    }

    private QuizAdditionalResponse generateQuizAdditionalByLLM(List<QuizQuestionResponse> responses) {
        StringBuilder prompt = new StringBuilder();
        for (QuizQuestionResponse questionResponse : responses) {
            prompt.append(questionResponse.getQuestion());
            prompt.append("\n");
        }

        HttpEntity<String> entity = new HttpEntity<>(prompt.toString(), headers);
        ResponseEntity<QuizAdditionalResponse> response = restTemplate.exchange(OPENAI_ADDRESS + "/api/quiz/generate_additional", HttpMethod.POST, entity, QuizAdditionalResponse.class);
        return response.getBody();
    }

    private List<Pair<String, String>> fetchGenerationDataByCategoryForUser(String category) {
        int tourId = Integer.parseInt(category.substring(category.indexOf("=")+1));

        TourDTO tour = this.tourService.findTourByTourId(tourId);

        List<Pair<String, String>> result = extractStringsFromTour(tour).values().stream().toList(); //first: content, second: image
        result = result.stream().filter((item) -> item.getFirst().length() > 100).toList();

        if (result.size() > 10) {
            Random random = new Random();
            int randomCount = 5 + random.nextInt(6);
            List<Pair<String, String>> mutableResult = new ArrayList<>(result);
            Collections.shuffle(mutableResult, random);
            result = mutableResult.subList(0, randomCount);
        }

        return result;
    }

    private Map<String, Pair<String, String>> extractStringsFromTour(TourDTO tour) {
        String[] res = getStrings(tour);
        Map<String, Pair<String, String>> map = new HashMap<>();

        for (int i = 1; i < res.length; i++) {
            try {
                String currentStop = res[i];
                if (currentStop == null || currentStop.trim().isEmpty()) {
                    continue;
                }

                String viennaHistoryWikiId = extractViennaHistoryWikiId(currentStop);
                if (viennaHistoryWikiId == null || viennaHistoryWikiId.trim().isEmpty()) {
                    continue;
                }

                String image = extractImageUrl(currentStop);

                String contentGerman = extractContentGerman(currentStop);
                if (contentGerman == null || contentGerman.trim().isEmpty()) {
                    continue;
                }

                map.put(viennaHistoryWikiId, Pair.of(contentGerman, image != null ? image : ""));

            } catch (Exception e) {
                logger.error("Error processing stop {}: {}", i, e.getMessage());
            }
        }

        if (map.isEmpty()) {
            throw new NoSuchElementException("No valid stops found on tour for quiz!");
        }

        return map;
    }

    private static String[] getStrings(TourDTO tour) {
        if (tour == null) {
            throw new RuntimeException("No tour available for quiz!");
        }

        String stops = tour.getStops();
        if (stops == null || stops.trim().isEmpty()) {
            throw new RuntimeException("No stops data available for quiz!");
        }

        if (!stops.contains("\"viennaHistoryWikiId\":")) {
            throw new RuntimeException("No Vienna history stops available for quiz!");
        }

        return stops.split("\"viennaHistoryWikiId\":");
    }

    private String extractViennaHistoryWikiId(String stopData) {
        if (stopData == null || !stopData.contains(",")) {
            return null;
        }

        try {
            int commaIndex = stopData.indexOf(",");
            if (commaIndex <= 0) {
                return null;
            }
            return stopData.substring(0, commaIndex).trim().replaceAll("^\"|\"$", "");
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    private String extractImageUrl(String stopData) {
        if (stopData == null) {
            return "";
        }

        try {
            String imageMarker = "\"imageUrls\":[";
            int imageStart = stopData.indexOf(imageMarker);
            if (imageStart == -1) {
                return "";
            }

            int imageDataStart = imageStart + imageMarker.length();
            if (imageDataStart >= stopData.length()) {
                return "";
            }

            String imageTillEnd = stopData.substring(imageDataStart);
            int imageEnd = imageTillEnd.indexOf("\"]");
            if (imageEnd == -1) {
                return "";
            }

            String images = imageTillEnd.substring(0, imageEnd);
            if (images.trim().isEmpty()) {
                return "";
            }

            if (images.startsWith("\"")) {
                images = images.substring(1);
            }

            String[] imageArr = images.split("\",\"");
            if (imageArr.length < 2) {
                return imageArr.length == 1 ? imageArr[0] : "";
            }

            String secondImage = imageArr[1];
            return "https://www.geschichtewiki.wien.gv.at/images/7/7d/RDF.png".equals(secondImage) ? "" : secondImage;

        } catch (Exception e) {
            return "";
        }
    }

    private String extractContentGerman(String stopData) {
        if (stopData == null) {
            return null;
        }

        try {
            String contentMarker = "contentGerman\":";
            int contentStart = stopData.indexOf(contentMarker);
            if (contentStart == -1) {
                return null;
            }

            int contentDataStart = contentStart + contentMarker.length();
            if (contentDataStart >= stopData.length()) {
                return null;
            }

            String contentTillEnd = stopData.substring(contentDataStart);

            if (contentTillEnd.startsWith("\"")) {
                contentTillEnd = contentTillEnd.substring(1);
            }

            int contentEnd = contentTillEnd.indexOf("\",");
            if (contentEnd == -1) {
                contentEnd = contentTillEnd.indexOf("\"");
                if (contentEnd == -1) {
                    return contentTillEnd.trim();
                }
            }

            return contentTillEnd.substring(0, contentEnd).trim();

        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    private List<QuestionDTO> mapQuestionResponsesToQuestionDTOList(List<QuizQuestionResponse> responses, int quiz) {
        List<QuestionDTO> result = new ArrayList<>();

        for (QuizQuestionResponse response : responses) {
            QuestionDTO dto = new QuestionDTO();

            dto.setQuiz(quiz);
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

    private void enrichResponseWithImageData(List<QuizQuestionResponse> responses, List<String> images) {
        for (int i = 0; i < responses.size(); i++) {
            logger.info("IMAGE: {}", images.get(i));
            responses.get(i).setImage(images.get(i));
        }
    }

    private void addUsersToQuiz(List<UUID> users, int quiz) {
        for (UUID user : users) {
            QuizUserEntity quizUserEntity = new QuizUserEntity();
            quizUserEntity.setQuiz(quiz);
            quizUserEntity.setPlayer(user);
            quizUserEntity.setCreatedAt(LocalDateTime.now());
            quizUserRepository.save(quizUserEntity);
        }
    }

    private List<QuestionResultsEntity> mapToQuestionResultsEntity (QuizResultDTO dto) {
        List<QuestionResultsDTO> questionResultsList = dto.getQuestionResults();
        List<QuestionResultsEntity> list = new ArrayList<>();

        if (questionResultsList == null || questionResultsList.isEmpty()) {
            return list;
        }

        for (QuestionResultsDTO questionResults : questionResultsList) {
            QuestionResultsEntity entity = new QuestionResultsEntity();

            entity.setQuestion(questionResults.getQuestion());
            entity.setPlayer(questionResults.getPlayer());
            entity.setCorrect(questionResults.getCorrect());
            entity.setCreatedAt(LocalDateTime.now());

            list.add(entity);
        }

        return list;
    }

    private QuizResultDTO getResultsForUserForQuiz(UUID user, int quiz) {
        QuizResultDTO result = new QuizResultDTO();
        result.setQuiz(quiz);
        result.setQuestionResults(new ArrayList<>());
        result.setFriendResults(new ArrayList<>());
        List<QuestionEntity> questions = questionRepository.findByQuiz(quiz);
        List<QuizFriendResultDTO> friendResultDTOs = new ArrayList<>();

        for (QuestionEntity question : questions) {
            int questionId = question.getId();
            List<QuestionResultsEntity> questionResults = questionResultsRepository.findByQuestion(questionId);

            QuestionResultsDTO questionResultsDTO = new QuestionResultsDTO();
            for (QuestionResultsEntity questionResultsEntity : questionResults) {
                UUID player = questionResultsEntity.getPlayer();
                if (user.equals(player)) {
                    questionResultsDTO = mapToQuestionResultsDTO(questionResultsEntity);
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
                        newFriend.setQuiz(quiz);
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

            result.getQuestionResults().add(questionResultsDTO);
        }

        result.setFriendResults(friendResultDTOs);
        return result;
    }
}