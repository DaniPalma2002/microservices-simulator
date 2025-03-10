package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.EventHandler;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.QuizEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate.Quiz;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate.QuizRepository;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class QuizEventHandler extends EventHandler {
    private QuizRepository quizRepository;
    protected QuizEventProcessing quizEventProcessing;

    public QuizEventHandler(QuizRepository quizRepository, QuizEventProcessing quizEventProcessing) {
        this.quizRepository = quizRepository;
        this.quizEventProcessing = quizEventProcessing;
    }

    public Set<Integer> getAggregateIds() {
        return quizRepository.findAll().stream().map(Quiz::getAggregateId).collect(Collectors.toSet());
    }

}
