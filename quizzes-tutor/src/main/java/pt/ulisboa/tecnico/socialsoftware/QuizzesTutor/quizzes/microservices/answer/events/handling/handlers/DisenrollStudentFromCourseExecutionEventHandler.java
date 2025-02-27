package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.QuizAnswerEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.events.handling.handlers.QuizAnswerEventHandler;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.events.publish.DisenrollStudentFromCourseExecutionEvent;

public class DisenrollStudentFromCourseExecutionEventHandler extends QuizAnswerEventHandler {
    public DisenrollStudentFromCourseExecutionEventHandler(QuizAnswerRepository quizAnswerRepository, QuizAnswerEventProcessing quizAnswerEventProcessing) {
        super(quizAnswerRepository, quizAnswerEventProcessing);
    }

    @Override
    public void handleEvent(Integer subscriberAggregateId, Event event) {
        this.quizAnswerEventProcessing.processDisenrollStudentEvent(subscriberAggregateId, (DisenrollStudentFromCourseExecutionEvent) event);
    }
}
