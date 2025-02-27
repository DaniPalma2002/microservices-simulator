package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.factories;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate.*;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.SagaQuiz;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.dtos.SagaQuizDto;

import java.util.Set;

@Service
@Profile("sagas")
public class SagasQuizFactory implements QuizFactory {
    @Override
    public Quiz createQuiz(Integer aggregateId, QuizCourseExecution quizCourseExecution, Set<QuizQuestion> quizQuestions, QuizDto quizDto, QuizType quizType) {
        return new SagaQuiz(aggregateId, quizCourseExecution, quizQuestions, quizDto, quizType);
    }

    @Override
    public Quiz createQuizFromExisting(Quiz existingQuiz) {
        return new SagaQuiz((SagaQuiz) existingQuiz);
    }
    
    @Override
    public QuizDto createQuizDto(Quiz quiz) {
        return new SagaQuizDto(quiz);
    }
}
