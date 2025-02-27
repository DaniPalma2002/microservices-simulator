package pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate;

import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.Question;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.QuestionCourse;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.QuizzesTutor.quizzes.microservices.question.aggregate.QuestionTopic;

import java.util.List;

public interface QuestionFactory {
    Question createQuestion(Integer aggregateId, QuestionCourse questionCourse, QuestionDto questionDto, List<QuestionTopic> questionTopics);
    Question createQuestionFromExisting(Question existingQuestion);
    QuestionDto createQuestionDto(Question question);
}
