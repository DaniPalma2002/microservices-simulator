package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate;

import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.quiz.aggregate.QuizCourseExecution;

import java.util.Set;

public interface QuizFactory {
    Quiz createQuiz(Integer aggregateId, QuizCourseExecution quizCourseExecution, Set<QuizQuestion> quizQuestions, QuizDto quizDto, QuizType quizType);
    Quiz createQuizFromExisting(Quiz existingQuiz);
    QuizDto createQuizDto(Quiz quiz);
}
