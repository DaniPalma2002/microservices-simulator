package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.QuizEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.events.publish.DeleteCourseExecutionEvent;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.events.handling.handlers.QuizEventHandler;

public class DeleteCourseExecutionEventHandler extends QuizEventHandler {
    public DeleteCourseExecutionEventHandler(QuizRepository quizRepository, QuizEventProcessing quizEventProcessing) {
        super(quizRepository, quizEventProcessing);
    }

    @Override
    public void handleEvent(Integer subscriberAggregateId, Event event) {
        this.quizEventProcessing.processDeleteCourseExecutionEvent(subscriberAggregateId, (DeleteCourseExecutionEvent) event);
    }
}
