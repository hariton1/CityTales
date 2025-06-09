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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuizService {

    private final QuestionRepository questionRepository;
    private final QuestionResultsRepository questionResultsRepository;
    private final QuizRepository quizRepository;
    private final QuizUserRepository quizUserRepository;

    public QuizService(QuestionRepository questionRepository, QuestionResultsRepository questionResultsRepository, QuizRepository quizRepository, QuizUserRepository quizUserRepository) {
        this.questionRepository = questionRepository;
        this.questionResultsRepository = questionResultsRepository;
        this.quizRepository = quizRepository;
        this.quizUserRepository = quizUserRepository;
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

    public QuizDTO saveQuiz(QuizDTO quiz) {
        QuizEntity quizEntity = quizRepository.save(mapToQuizEntity(quiz));
        List<QuestionEntity> questionEntities = questionRepository.saveAll(mapToQuestionEntityList(quiz.getQuestions()));

        return mapToQuizDTO(quizEntity, questionEntities);
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
}