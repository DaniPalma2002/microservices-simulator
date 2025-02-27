package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.QuizEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.events.publish.DeleteQuestionEvent;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.events.handling.handlers.QuizEventHandler;

public class DeleteQuestionEventHandler extends QuizEventHandler {
    public DeleteQuestionEventHandler(QuizRepository quizRepository, QuizEventProcessing quizEventProcessing) {
        super(quizRepository, quizEventProcessing);
    }

    @Override
    public void handleEvent(Integer subscriberAggregateId, Event event) {
        this.quizEventProcessing.processDeleteQuizQuestionEvent(subscriberAggregateId, (DeleteQuestionEvent) event);
    }
}
