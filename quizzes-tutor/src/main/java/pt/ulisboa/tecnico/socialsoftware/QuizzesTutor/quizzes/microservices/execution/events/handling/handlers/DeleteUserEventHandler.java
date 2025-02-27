package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.coordination.eventProcessing.CourseExecutionEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.events.handling.handlers.CourseExecutionEventHandler;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.user.events.publish.DeleteUserEvent;

public class DeleteUserEventHandler extends CourseExecutionEventHandler {
    public DeleteUserEventHandler(CourseExecutionRepository courseExecutionRepository, CourseExecutionEventProcessing courseExecutionEventProcessing) {
        super(courseExecutionRepository, courseExecutionEventProcessing);
    }

    @Override
    public void handleEvent(Integer subscriberAggregateId, Event event) {
        this.courseExecutionEventProcessing.processDeleteUserEvent(subscriberAggregateId, (DeleteUserEvent) event);
    }
}
