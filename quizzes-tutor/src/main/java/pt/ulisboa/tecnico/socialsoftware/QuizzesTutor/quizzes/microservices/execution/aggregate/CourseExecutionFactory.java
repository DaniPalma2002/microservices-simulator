package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate;

import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecutionCourse;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecutionDto;

public interface CourseExecutionFactory {
    pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecution createCourseExecution(Integer aggregateId, pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecutionDto courseExecutionDto, CourseExecutionCourse courseExecutionCourse);
    pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecution createCourseExecutionFromExisting(pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.execution.aggregate.CourseExecution existingCourseExecution);
    CourseExecutionDto createCourseExecutionDto(CourseExecution courseExecution);
}
