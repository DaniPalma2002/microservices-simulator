package pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.events.handling.handlers;

import pt.ulisboa.tecnico.socialsoftware.ms.domain.event.EventHandler;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.causal.coordination.eventProcessing.CourseExecutionEventProcessing;
import pt.ulisboa.tecnico.socialsoftware.ms.quizzes.microservices.execution.aggregate.CourseExecution;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class CourseExecutionEventHandler extends EventHandler {
    private CourseExecutionRepository courseExecutionRepository;
    protected CourseExecutionEventProcessing courseExecutionEventProcessing;

    public CourseExecutionEventHandler(CourseExecutionRepository courseExecutionRepository, CourseExecutionEventProcessing courseExecutionEventProcessing) {
        this.courseExecutionRepository = courseExecutionRepository;
        this.courseExecutionEventProcessing = courseExecutionEventProcessing;
    }

    public Set<Integer> getAggregateIds() {
        return courseExecutionRepository.findAll().stream().map(CourseExecution::getAggregateId).collect(Collectors.toSet());
    }

}
