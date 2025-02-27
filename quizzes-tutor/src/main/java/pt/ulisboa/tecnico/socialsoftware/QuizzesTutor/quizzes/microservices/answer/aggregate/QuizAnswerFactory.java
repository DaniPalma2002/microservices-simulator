package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate;

import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerCourseExecution;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnswerStudent;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.AnsweredQuiz;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuizAnswerDto;

public interface QuizAnswerFactory {
    pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuizAnswer createQuizAnswer(Integer aggregateId, AnswerCourseExecution answerCourseExecution, AnswerStudent answerStudent, AnsweredQuiz answeredQuiz);
    pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuizAnswer createQuizAnswerFromExisting(pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.answer.aggregate.QuizAnswer existingAnswer);
    QuizAnswerDto createQuizAnswerDto(QuizAnswer quizAnswer);
}
