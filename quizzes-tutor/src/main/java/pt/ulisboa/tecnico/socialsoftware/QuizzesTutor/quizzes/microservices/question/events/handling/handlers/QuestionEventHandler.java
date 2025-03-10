package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.EventHandler;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.QuestionEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.Question;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.QuestionRepository;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class QuestionEventHandler extends EventHandler {
    private QuestionRepository questionRepository;
    protected QuestionEventProcessing questionEventProcessing;

    public QuestionEventHandler(QuestionRepository questionRepository, QuestionEventProcessing questionEventProcessing) {
        this.questionRepository = questionRepository;
        this.questionEventProcessing = questionEventProcessing;
    }

    public Set<Integer> getAggregateIds() {
        return questionRepository.findAll().stream().map(Question::getAggregateId).collect(Collectors.toSet());
    }

}
