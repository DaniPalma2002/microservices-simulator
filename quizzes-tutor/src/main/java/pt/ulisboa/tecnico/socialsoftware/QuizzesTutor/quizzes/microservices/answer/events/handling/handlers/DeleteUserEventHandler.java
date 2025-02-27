package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.QuizAnswerEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.events.handling.handlers.QuizAnswerEventHandler;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.events.publish.DeleteUserEvent;

public class DeleteUserEventHandler extends QuizAnswerEventHandler {
    public DeleteUserEventHandler(QuizAnswerRepository quizAnswerRepository, QuizAnswerEventProcessing quizAnswerEventProcessing) {
        super(quizAnswerRepository, quizAnswerEventProcessing);
    }

    @Override
    public void handleEvent(Integer subscriberAggregateId, Event event) {
        this.quizAnswerEventProcessing.processDeleteUserEvent(subscriberAggregateId, (DeleteUserEvent) event);
    }
}
