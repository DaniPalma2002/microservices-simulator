package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate.*;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.aggregate.GenericSagaState;
import pt.ulisboa.tecnico.socialsoftware.ms.sagas.aggregate.SagaAggregate;

import java.util.Set;
@Entity
public class SagaQuiz extends Quiz implements SagaAggregate {
    private SagaState sagaState;
    
    public SagaQuiz() {
        this.sagaState = GenericSagaState.NOT_IN_SAGA;
    }

    public SagaQuiz(Integer aggregateId, QuizCourseExecution quizCourseExecution, Set<QuizQuestion> quizQuestions, QuizDto quizDto, QuizType quizType) {
        super(aggregateId, quizCourseExecution, quizQuestions, quizDto, quizType);
        this.sagaState = GenericSagaState.NOT_IN_SAGA;
    }

    public SagaQuiz(SagaQuiz other) {
        super(other);
        this.sagaState = other.getSagaState();
    }

    @Override
    public void setSagaState(SagaState state) {
        this.sagaState = state;
    }

    @Override
    public SagaState getSagaState() {
        return this.sagaState;
    }
}
