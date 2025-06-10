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

    private final RestTemplate restTemplate = new RestTemplate();
    private final String OPENAI_ADDRESS = "http://openai-adapter:8088/";

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

    public QuizDTO saveQuiz(String category, List<UUID> users) {
        //todo call build creation criteria based on users and category (tours, interests, fun facts ect.)
        String prompt = "Die Staatsoper war in der Monarchie bis 1918 - wie das Burgtheater - ein Hoftheater, das vom Obersthofmeisteramt des Kaisers beaufsichtigt wurde. Das Hofärar (öffentliche Gelder in der Verwaltung des Kaiserhofes) sorgte für die Finanzierung. In der Republik wird sie als Staatstheater geführt (siehe Bundestheater), die Finanzierung obliegt der Bundesregierung.Bevor das alte Kärntnertortheater 1870 demoliert wurde, ging man an die Erbauung eines neuen Opernhauses, das (über einem Teil des ehemaligen Stadtgrabens, auf den Stadterweiterungsgründen, gelegen) von Eduard van der Nüll und August Sicard von Sicardsburg in romantisch-historisierendem Stil errichtet wurde (Wettbewerbsausschreibung am 10. Juni 1860, Entscheidung der Jury am 28. Oktober 1861, Grundsteinlegung am 20. Mai 1863, Hauptgesimsgleiche am 7. Oktober 1865, Beendigung der Innenausgestaltung im Frühjahr 1869). Nach dem Tod der beiden Architekten 1868 wurde die Hofoper von Gustav Gugitz und Josef Storck vollendet. Das Opernhaus wurde am 25. Mai 1869 mit Mozarts \"Don Giovanni\" eröffnet (Generalprobe 30. April). Bei dem durch einen Fliegerangriff am 12. März 1945 hervorgerufenen Brand wurde die Staatsoper bis auf die Hauptmauern und das große Stiegenhaus am Ring zerstört.In der offenen (heute verglasten) Loggia ober den Haupteingängen Gemälde von Moritz von Schwind, in den fünf Öffnungen allegorischen Bronzefiguren von Ernst Julius Hähnel, auf den beiden Postamenten oberhalb der Loggia je ein Pegasus vom selben Meister (zwei ursprünglich für diesen Platz hergestellte Pegasusfiguren von Vinzenz Pilz erwiesen sich als zu monumental, wurden im April 1870 wieder von ihren Piedestalen entfernt und nach Philadelphia, United States of America, verkauft). Restaurierung der Fassade am Ring ab 2004.Die sieben Statuen im Treppenhaus (Bildhauer Josef Gasser) stellen die sieben freien Künste dar (1869). An der Ausstattung der Räume beteiligten sich die damals bekanntesten Künstler Wiens, wie Carl Rahl, Moritz von Schwind, Johann Preleuthner, Franz Dobiaschofsky, Franz Schönthaler, Hanns und Josef Gasser, Eduard Bitterlich, Christian Griepenkerl, Eduard Engerth, Franz Melnitzky. Die Hochreliefs an der Hinterwand des Stiegenhauses (\"Oper\", \"Ballett\") schuf Johann Baptist Preleuthner , die darunter angebrachten Medaillonreliefs (Nüll, Sicard) Josef Cesar.1905 spendeten die Großindustriellen A. und F. Böhler zwei Kandelaber mit Bronzefiguren, die vor der Oper am Ring aufgestellt wurden. Die eine der beiden Statuen - sie stammten von Fritz Zerritsch, stellte Siegfried dar, das Schwert in der Rechten, in der Linken den Nibelungenring, zu Füßen den getöteten Fafner. Der Held hielt den Kopf horchend erhoben: Es war die Szene, wo er in Wagners Oper der Stimme des Waldvögleins lauschte. Die zweite Figur zeigte den Steinernen Gast mit dem sterbenden Don Juan zu Füßen (Mozart). Beide Figuren wurden während des Zweiten Weltkriegs entfernt, sie sollten eingeschmolzen werden. Nachforschungen über ihr Schicksal ergaben widersprüchliche Angaben: Ein Gewährsmann versichert, er habe beide Statuen 1947 bei einer Firma am Donaukanal gesehen; ihr Metall sei zu schlecht gewesen, um für militärische Zwecke eingeschmolzen zu werden. Später seien sie von dort verschwunden gewesen. Der zweite Gewährsmann behauptet, die beiden Figuren seien gar nicht während des Kriegs abmontiert worden, sondern noch Anfang 1946 vor der Oper gestanden, die russischen Besatzungssoldaten hätten ihre Telephonleitungen an ihnen befestigt, und erst in diesem Jahr seien sie dann verschwunden.Bei dem nach wenigen Jahren einsetzenden Wiederaufbau übernahm Erich Boltenstern die Neugestaltung des Zuschauerraums, der Stiegenaufgänge zwischen den Rängen, der Publikumsgarderoben und der Pausenräume in den oberen Rängen. (Die Deckenbilder des alten Zuschauerraums von Carl Rahl sowie der vom selben Künstler gestaltete Vorhang waren ein Raub der Flammen geworden. Lediglich die Fresken von Eduard Engerth im Kaisersaal konnten teilweise gerettet und später übertragen werden.) Zeno Kosak gestaltete den Gobelinsaal, die Architekten Otto Prossinger und Felix Cewela restaurierten die Seitenfronten und den Marmorsaal, den gesamten Neubau der Bühne besorgte die Bauleitung des Bundesministeriums für Handel und Wiederaufbau in Eigenregie. Rudolf Hermann Eisenmenger schuf die Kartons für die Gobelins im Gobelinsaal sowie die Monumentalmalerei auf dem eisernen Vorhang (Orpheus und Eurydike). Nach Kriegsende wurde der Spielbetrieb im Theater an der Wien (6. Oktober 1945) und in der Volksoper (1. Mai 1945; Figaros Hochzeit) aufgenommen. Die restaurierte Oper wurde am 5. November 1955 mit einer festlichen Aufführung von Beethovens \"Fidelio\" unter Karl Böhm eröffnet. Es war ein gesellschaftliches Ereignis ersten Rangs, aus allen Teilen der Erde kamen prominenteste Gäste nach Wien, um diese Eröffnung mitzuerleben.Seit 1998 erhält der Eiserne Vorhang in jeder Saison ein durch ein besonderes Ereignis (beispielsweise 2003/2004 Parsifal-Jahr) inspiriertes Bild. Es handelt sich dabei um Projekte des \"museum in progress\" in Kooperation mit der Wiener Staatsoper, finanziell ermöglicht durch Sponsoren. Bisher wurden folgende Künstler(innen) beauftragt:Übersiedlung aus dem Hanuschhof (1., Hanuschgasse 3/Goethegasse 1) in die neu adaptierten Räume 1, Operngasse 2 (Eröffnung 14. Februar 2004).Ehrenmitglieder Staatsoper.";

        //todo call chatGPT to create a new quiz based on category
        QuizResponse answer = generateQuizByLLM(prompt);

        //todo save quiz
        QuizDTO result = new QuizDTO();

        result.setId(100);
        result.setName("persisted Quiz");
        result.setDescription(answer.getAnswer());
        result.setCategory(category);
        result.setCreator(users.getFirst());
        result.setQuestions(null);
        result.setCreatedAt(LocalDateTime.now());

        //QuizEntity quizEntity = quizRepository.save(mapToQuizEntity(quiz));
        //List<QuestionEntity> questionEntities = questionRepository.saveAll(mapToQuestionEntityList(quiz.getQuestions()));
        //return mapToQuizDTO(quizEntity, questionEntities);

        //todo add users to quiz via quiz_user table

        //todo return quiz
        return result;
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
}