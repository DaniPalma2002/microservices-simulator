package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.factories;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.*;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.SagaQuestion;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.dtos.SagaQuestionDto;

import java.util.List;

@Service
@Profile("sagas")
public class SagasQuestionFactory implements QuestionFactory {

    @Override
    public Question createQuestion(Integer aggregateId, QuestionCourse questionCourse, QuestionDto questionDto, List<QuestionTopic> questionTopics) {
        return new SagaQuestion(aggregateId, questionCourse, questionDto, questionTopics);
    }

    @Override
    public Question createQuestionFromExisting(Question existingQuestion) {
        return new SagaQuestion((SagaQuestion) existingQuestion);
    }

    @Override
    public SagaQuestionDto createQuestionDto(Question question) {
        return new SagaQuestionDto(question);
    }
    
}
