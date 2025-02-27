package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate;

import java.util.Optional;

public interface QuizAnswerCustomRepository {
    Optional<Integer> findQuizAnswerIdByQuizIdAndUserId(Integer quizAggregateId, Integer studentAggregateId);
}
