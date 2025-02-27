package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.sagas.aggregates.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.course.aggregate.CourseCustomRepository;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.course.aggregate.CourseRepository;

import java.util.Optional;

@Profile("sagas")
@Service
public class CourseCustomRepositorySagas implements CourseCustomRepository {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Optional<Integer> findCourseIdByName(String courseName) {
        return courseRepository.findCourseIdByNameForSaga(courseName);
    }
}