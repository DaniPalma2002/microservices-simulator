package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.events.publish;

import jakarta.persistence.Entity;
import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.Event;

@Entity
public class DeleteCourseExecutionEvent extends Event {
    public DeleteCourseExecutionEvent() {}

    public DeleteCourseExecutionEvent(Integer aggregateId) {
        super(aggregateId);
    }
}
